/**
 * 
 * Copyright (c) 2013-2015, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
 * 
 * This file is part of Emfconnector, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.technologyadapter.emf.rm;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.openflexo.foundation.resource.FileIODelegate;
import org.openflexo.foundation.resource.FlexoIODelegate;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.InJarIODelegate;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.metamodel.io.EMFMetaModelConverter;

/**
 * Default implementation of a {@link JarBasedMetaModelResource}
 * 
 * @author sylvain
 *
 */
public abstract class JarBasedMetaModelResourceImpl extends EMFMetaModelResourceImpl implements JarBasedMetaModelResource {

	protected static final Logger logger = Logger.getLogger(JarBasedMetaModelResourceImpl.class.getPackage().getName());

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.resource.FlexoResource#loadResourceData()
	 */
	@Override
	public EMFMetaModel loadResourceData() throws ResourceLoadingCancelledException {

		if (isLoaded()) {
			return getMetaModelData();
		}

		// A given metamodel URI must be backed by a single class loader. The very same metamodel may be discovered twice
		// (eg. when its resource center happens to be registered twice), in which case only the first resource is registered
		// in the technology context manager. Loading the other one would build a second JarClassLoader, whose EPackage
		// silently overrides the first one in the global EPackage.Registry: model objects would then be instantiated by a
		// factory coming from a class loader, while their EMF Resource comes from another one, and every cross-class-loader
		// access fails (ClassCastException, or UnsupportedOperationException on derived features). We thus delegate here to
		// the resource which is registered for that URI.
		EMFMetaModelResource registeredResource = getTechnologyAdapter().getTechnologyContextManager().getMetaModelResourceByURI(getURI());
		if (registeredResource != null && registeredResource != this) {
			EMFMetaModel registeredMetaModel = registeredResource.getMetaModelData();
			setPackage(registeredResource.getPackage());
			if (registeredResource instanceof JarBasedMetaModelResource) {
				setEMFResourceFactory(((JarBasedMetaModelResource) registeredResource).getEMFResourceFactory());
			}
			resourceData = registeredMetaModel;
			return registeredMetaModel;
		}

		// Same invariant, but at JVM scope: the guard above only spans ONE EMFTechnologyContextManager, while
		// EPackage.Registry.INSTANCE is static, and retrieveClassLoader() builds a brand new JarInDirClassLoader on every
		// call. Two successive service managers in a single JVM (several test classes, or one service manager per
		// .fmlscript) thus used to load the very same metamodel jar under distinct class loaders. From the 2nd load on, the
		// generated <Xxx>FactoryImpl.init() found the previous session's factory in the global registry and failed its cast
		// ("City1FactoryImpl cannot be cast to City1Factory", swallowed by init() but printed by EcorePlugin.INSTANCE.log),
		// then <Xxx>PackageImpl.init() overrode the registry entry, leaving the last loaded EPackage to win JVM-wide and
		// retaining every abandoned class loader. We therefore reuse the class loader which already owns that URI in the
		// global registry, so that one metamodel URI maps to one single set of classes for the whole JVM.
		// Beware: the registered EPackage may come from somewhere else than our jar (a dynamic EPackage built from an
		// .ecore, for instance), in which case its class loader knows nothing of our generated classes: we check that it
		// really does define the package class we are about to load, and fall back to a fresh class loader otherwise.
		// Still open: nothing unregisters our EPackages when the service manager stops, so a metamodel jar modified on disk
		// is not reloaded within a session, and the very first class loader is kept alive until the JVM exits.
		ClassLoader alreadyUsedClassLoader = null;
		EPackage alreadyRegisteredPackage = (getPackageClassName() != null ? EPackage.Registry.INSTANCE.getEPackage(getURI())
				: null);
		if (alreadyRegisteredPackage != null) {
			ClassLoader registeredClassLoader = alreadyRegisteredPackage.getClass().getClassLoader();
			if (registeredClassLoader != null) {
				try {
					if (registeredClassLoader.loadClass(getPackageClassName()).isInstance(alreadyRegisteredPackage)) {
						alreadyUsedClassLoader = registeredClassLoader;
					}
				}
				catch (ClassNotFoundException e) {
					// That EPackage was not loaded from our jar: ignore it and build our own class loader
				}
			}
		}

		EMFMetaModel result = null;
		Class<?> ePackageClass = null;
		ClassLoader classLoader = null;

		try {
			// Retrieve class loader to be used
			classLoader = (alreadyUsedClassLoader != null ? alreadyUsedClassLoader : getIODelegate().retrieveClassLoader());

			System.out.println("Reading EMF metamodel from " + getIODelegate());
			System.out.println("ClassLoader=" + classLoader);
			System.out.println("packageClassName=" + getPackageClassName());

			ePackageClass = classLoader.loadClass(getPackageClassName());
			System.out.println("ePackageClass=" + ePackageClass);

			if (ePackageClass != null) {
				Field ePackageField = ePackageClass.getField("eINSTANCE");
				if (ePackageField != null) {
					EPackage ePack = (EPackage) ePackageField.get(null);
					setPackage(ePack);
				}

				// And perform the load
				performLoadMetaModel(classLoader);

				if (getPackage() != null && getPackage().getNsURI().equalsIgnoreCase(getURI())) {

					EMFMetaModelConverter converter = new EMFMetaModelConverter(getTechnologyAdapter());
					result = converter.convertMetaModel(getPackage());
					result.setResource(this);
					resourceData = result;
				}

			}

			// System.out.println("Done loaded " + getURI());

			/*System.out.println("------------> Registering Metamodel " + result);
			
			System.out.println("Root concept " + result.getRootConcept());
			for (IFlexoOntologyClass<EMFTechnologyAdapter> iFlexoOntologyClass : result.getClasses()) {
				System.out.println("* Concept " + iFlexoOntologyClass);
				for (IFlexoOntologyFeatureAssociation<EMFTechnologyAdapter> iFlexoOntologyFeatureAssociation : iFlexoOntologyClass
						.getStructuralFeatureAssociations()) {
					System.out.println("   > " + iFlexoOntologyFeatureAssociation.getFeature().getName() + " : "
							+ iFlexoOntologyFeatureAssociation.getRange());
				}
			}*/
			/*for (IFlexoOntologyIndividual<EMFTechnologyAdapter> iFlexoOntologyIndividual : result.getIndividuals()) {
				System.out.println(" > Individual " + iFlexoOntologyIndividual + " of " + iFlexoOntologyIndividual.getTypes());
			}*/

			return result;

		} catch (ClassNotFoundException e) {
			logger.warning("Unable to load EMF MEtaModel: could not find some class");
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			logger.warning("Unable to load EMF MEtaModel: could not find some property");
			e.printStackTrace();
		} catch (SecurityException e) {
			logger.warning("Unable to load EMF MEtaModel:");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			logger.warning("Unable to load EMF MEtaModel:");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			logger.warning("Unable to load EMF MEtaModel:");
			e.printStackTrace();
		} catch (InstantiationException e) {
			logger.warning("Unable to load EMF MEtaModel:");
			e.printStackTrace();
		}
		return null;

	}

	protected void performLoadMetaModel(ClassLoader classLoader)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> resourceFactoryClass = classLoader.loadClass(getEMFResourceFactoryClassName());
		if (resourceFactoryClass != null) {
			setEMFResourceFactory((Resource.Factory) resourceFactoryClass.newInstance());
		}
		else {
			logger.warning("I will not be able to initialize EMF Model Factory for: " + getURI());
		}
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.resource.FlexoResource#save()
	 */
	@Override
	public void save() {
		logger.info("MetaModel is not supposed to be modified.");
	}

	@Override
	public Class<EMFMetaModel> getResourceDataClass() {
		return EMFMetaModel.class;
	}

	@Override
	public <I> EMFModelResource getInitialModelResource() {

		FlexoResourceCenter<I> rc = (FlexoResourceCenter<I>) getResourceCenter();
		System.out.println("rc=" + rc);
		I initialModel = rc.getEntry("Initial." + getModelFileExtension(), rc.getContainer((I) getIODelegate().getSerializationArtefact()));
		System.out.println("initialModel=" + initialModel);
		if (rc.exists(initialModel)) {
			System.out.println("Found initial model : " + initialModel);
			EMFModelResource returned = rc.getResource(initialModel, EMFModelResource.class);
			System.out.println("Return : " + returned);
			return returned;
		}
		return null;
	}

	/**
	 * Use the factory which is provided in configuration file
	 * 
	 */
	/*@Override
	protected Factory getEMFFactory() {
		return getEMFResourceFactory();
	}*/

	/**
	 * Creates a new ModelResource, for EMF, MetaModel decides wich type of serialization you should use!
	 * 
	 * @param flexoIODelegate
	 * @return
	 */
	@Override
	public Resource createEMFModelResource(FlexoIODelegate<?> flexoIODelegate) {

		// TODO: refactor this with IODelegate

		if (flexoIODelegate instanceof FileIODelegate) {
			return getEMFResourceFactory().createResource(
					org.eclipse.emf.common.util.URI.createFileURI(((FileIODelegate) flexoIODelegate).getFile().getAbsolutePath()));
		}

		if (flexoIODelegate instanceof InJarIODelegate) {
			try {
				InJarIODelegate inJarIODelegate = (InJarIODelegate) flexoIODelegate;
				JarEntry entry = inJarIODelegate.getInJarResource().getEntry();
				// TODO: Cannot use try-with-resource for jarFile below (breaks EMF connector)
				JarFile jarFile = inJarIODelegate.getInJarResource().getJarResource().getJarfile();
				File copiedFile = jarEntryAsFile(jarFile, entry);
				return getEMFResourceFactory().createResource(org.eclipse.emf.common.util.URI.createFileURI(copiedFile.getAbsolutePath()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// not implemented
		logger.warning("createEMFModelResource() for " + flexoIODelegate + " : not implemented");

		return null;
	}

}
