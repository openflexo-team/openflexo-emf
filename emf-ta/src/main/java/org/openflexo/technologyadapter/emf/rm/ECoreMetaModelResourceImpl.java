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
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.resource.FileIODelegate;
import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.foundation.resource.FlexoIODelegate;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.InJarIODelegate;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.rm.InJarResourceImpl;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.metamodel.io.EMFMetaModelConverter;
import org.openflexo.toolbox.FileSystemMetaDataManager;

/**
 * Default implementation of a {@link ECoreMetaModelResource}
 * 
 * @author sylvain
 *
 */
public abstract class ECoreMetaModelResourceImpl extends EMFMetaModelResourceImpl implements ECoreMetaModelResource {

	protected static final Logger logger = Logger.getLogger(ECoreMetaModelResourceImpl.class.getPackage().getName());

	private Resource resource;

	private Resource getEMFResource() {
		if (resource == null) {
			if (getIODelegate().getSerializationArtefact() instanceof File) {
				File ecoreFile = (File) getIODelegate().getSerializationArtefact();
				// Get the URI of the model file.
				// URI fileURI = URI.createPlatformPluginURI("platform:/plugin/org.eclipse.mymetamodel/model/MyMetaModel.ecore", false);
				URI fileURI = org.eclipse.emf.common.util.URI.createFileURI(ecoreFile.getAbsolutePath());
				// Demand load the resource for this file.
				resource = getTechnologyContextManager().getResourceSet().getResource(fileURI, true);
			}
			if (getIODelegate().getSerializationArtefact() instanceof InJarResourceImpl) {
				InJarResourceImpl inJarResource = ((InJarResourceImpl) getIODelegate().getSerializationArtefact());
				// System.out.println("URI: " + hop.getURI());
				URI jarEntryURI = org.eclipse.emf.common.util.URI.createURI(inJarResource.getURI());
				resource = getTechnologyContextManager().getResourceSet().getResource(jarEntryURI, true);
			}
			else {
				logger.warning("Don't know how to handle " + getIODelegate().getSerializationArtefact().getClass());
				return null;
			}
		}
		return resource;
	}

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

		EMFMetaModelConverter converter = new EMFMetaModelConverter(getTechnologyAdapter());
		resourceData = converter.convertMetaModel(getEMFResource());
		setPackage(converter.getRootPackage(getEMFResource()));
		resourceData.setResource(this);

		logger.info("Registering " + resourceData.getRootPackage() + " for " + getURI());
		getTechnologyContextManager().getResourceSet().getPackageRegistry().put(getURI(), resourceData.getRootPackage());

		// System.out.println("result=" + resourceData);
		// System.out.println("root_package=" + getPackage());
		// System.out.println("all_classes=" + resourceData.getAccessibleClasses());
		// System.out.println("classes=" + resourceData.getClasses());

		return resourceData;
	}

	/**
	 * Use the factory which is provided in configuration file
	 * 
	 */
	/*@Override
	protected Factory getEMFFactory() {
		return getTechnologyContextManager().getXMIResourceFactory();
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
			Resource returned = getTechnologyContextManager().getResourceSet().createResource(
					org.eclipse.emf.common.util.URI.createFileURI(((FileIODelegate) flexoIODelegate).getFile().getAbsolutePath()));
			return returned;
		}

		if (flexoIODelegate instanceof InJarIODelegate) {
			try {
				InJarIODelegate inJarIODelegate = (InJarIODelegate) flexoIODelegate;
				JarEntry entry = inJarIODelegate.getInJarResource().getEntry();
				// TODO: Cannot use try-with-resource for jarFile below (breaks EMF connector)
				JarFile jarFile = inJarIODelegate.getInJarResource().getJarResource().getJarfile();
				File copiedFile = jarEntryAsFile(jarFile, entry);
				return getTechnologyContextManager().getResourceSet()
						.createResource(org.eclipse.emf.common.util.URI.createFileURI(copiedFile.getAbsolutePath()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// not implemented
		logger.warning("createEMFModelResource() for " + flexoIODelegate + " : not implemented");

		return null;
	}

	private ECoreMetaData metaData;

	@Override
	public <I> ECoreMetaData getMetaData(FlexoResourceCenter<I> resourceCenter) {
		if (metaData == null) {
			metaData = findMetaData(resourceCenter, false);
		}
		return metaData;
	}

	private <I> ECoreMetaData findMetaData(FlexoResourceCenter<I> resourceCenter, boolean forceRebuild) {
		if (resourceCenter instanceof FlexoProject) {
			resourceCenter = ((FlexoProject<I>) resourceCenter).getDelegateResourceCenter();
		}

		if (resourceCenter instanceof FileSystemBasedResourceCenter) {
			FileSystemMetaDataManager metaDataManager = ((FileSystemBasedResourceCenter) resourceCenter).getMetaDataManager();
			File file = (File) getIODelegate().getSerializationArtefact();

			if (!forceRebuild && (file.lastModified() < metaDataManager.metaDataLastModified(file))) {
				// OK, in this case the metadata file is there and more recent than xml file
				// Attempt to retrieve metadata from cache
				return new ECoreMetaData(metaDataManager, file);
			}
			else {
				// No way, metadata are either not present or older than file version, we should parse XML file, continuing...
			}
		}

		// System.out.println("Retrieve info from file for " + this);

		ECoreMetaData returned = new ECoreMetaData(resourceCenter.getXMLRootElementInfo((I) getIODelegate().getSerializationArtefact()));

		if (resourceCenter instanceof FileSystemBasedResourceCenter && returned != null) {
			// Save metadata !!!
			FileSystemMetaDataManager metaDataManager = ((FileSystemBasedResourceCenter) resourceCenter).getMetaDataManager();
			File file = (File) getIODelegate().getSerializationArtefact();
			returned.save(metaDataManager, file);
		}

		return returned;
	}

}
