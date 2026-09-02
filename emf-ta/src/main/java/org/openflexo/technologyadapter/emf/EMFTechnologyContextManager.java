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

package org.openflexo.technologyadapter.emf;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IndividualOfClass;
import org.openflexo.foundation.ontology.technologyadapter.FlexoOntologyTechnologyContextManager;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.technologyadapter.emf.metamodel.EMFClassClass;
import org.openflexo.technologyadapter.emf.metamodel.EMFEnumClass;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelResource;
import org.openflexo.technologyadapter.emf.rm.EMFModelResource;
import org.openflexo.technologyadapter.emf.rm.JarBasedMetaModelResource;

public class EMFTechnologyContextManager extends FlexoOntologyTechnologyContextManager<EMFTechnologyAdapter> {

	protected static final Logger logger = Logger.getLogger(EMFTechnologyContextManager.class.getPackage().getName());

	/** Stores all known metamodels where key is the URI of metamodel */
	protected Map<String, EMFMetaModelResource> metamodels = new HashMap<>();
	/** Stores all known metamodels where key is the URI of profiles (UML) */
	protected Map<String, JarBasedMetaModelResource> profiles = new HashMap<>();
	/** Stores all known models where key is the URI of model */
	protected Map<String, EMFModelResource> models = new HashMap<>();

	/** Stores a reference to EMF Registry instance in order to register every MM available */

	protected Resource.Factory.Registry EMFRscFactoryRegistry = Resource.Factory.Registry.INSTANCE;
	protected EPackage.Registry EMFPackageRegistry = EPackage.Registry.INSTANCE;
	protected Map<String, Object> EMFExtensionToFactoryMap;

	/** What we put in the two registries above, together with what was there before, so that stop() may restore the initial state */
	private final List<GlobalRegistryEntry> globalRegistryEntries = new ArrayList<>();

	private final ResourceSet resourceSet;
	public final Factory ECORE_RESOURCE_FACTORY = new EcoreResourceFactoryImpl();
	public final Factory XMI_RESOURCE_FACTORY = new XMIResourceFactoryImpl();

	public EMFTechnologyContextManager(EMFTechnologyAdapter adapter, FlexoResourceCenterService resourceCenterService) {
		super(adapter, resourceCenterService);
		EMFExtensionToFactoryMap = EMFRscFactoryRegistry.getExtensionToFactoryMap();
		// This enables working with UML Models
		UMLResourcesUtil.initGlobalRegistries();

		resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", ECORE_RESOURCE_FACTORY);
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", XMI_RESOURCE_FACTORY);
	}

	public ResourceSet getResourceSet() {
		return resourceSet;
	}

	public EMFModelResource getModel(File modelFile) {
		return models.get(modelFile);
	}

	/**
	 * Called when a new meta model was registered, notify the {@link TechnologyContextManager}
	 * 
	 * @param newModel
	 */
	public void registerMetaModel(EMFMetaModelResource newMetaModelResource) {
		String mmURI = newMetaModelResource.getURI();
		EMFMetaModelResource existingMM = metamodels.get(mmURI);
		if (existingMM == null) {
			registerResource(newMetaModelResource);
			metamodels.put(mmURI, newMetaModelResource);
			registerInGlobalRegistry(EMFExtensionToFactoryMap, newMetaModelResource.getModelFileExtension(),
					newMetaModelResource.getEMFResourceFactory());

			if (!newMetaModelResource.getModelFileExtension().equals("ecore")) {
				resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(newMetaModelResource.getModelFileExtension(),
						newMetaModelResource.getEMFResourceFactory());
			}

			EPackage ePackage = newMetaModelResource.getPackage();
			// System.out.println("******* Register " + newMetaModelResource + " rc=" + newMetaModelResource.getResourceCenter());
			if (!EMFPackageRegistry.containsKey(mmURI) && ePackage != null) {
				registerInGlobalRegistry(EMFPackageRegistry, mmURI, ePackage);
			}
			else {
				logger.warning("EMF Metamodel already exists in registry URI: " + newMetaModelResource.getURI() + " resource from: "
						+ newMetaModelResource.getIODelegate().getSerializationArtefact());
			}
		}
		else {
			// TODO : xtof, manage duplicate URIs
			logger.warning(" There already exists a MM with that URI => I will not register this one!");
		}
	}

	/**
	 * Called when a new profile was registered, notify the {@link TechnologyContextManager}
	 * 
	 * @param newModel
	 */
	public void registerProfile(JarBasedMetaModelResource newMetaModelResource) {
		String mmURI = newMetaModelResource.getURI();
		EMFMetaModelResource existingMM = profiles.get(mmURI);
		if (existingMM == null) {
			registerResource(newMetaModelResource);
			profiles.put(mmURI, newMetaModelResource);
			EPackage ePackage = newMetaModelResource.getPackage();
			if (!EMFPackageRegistry.containsKey(mmURI) && ePackage != null) {
				registerInGlobalRegistry(EMFPackageRegistry, newMetaModelResource.getURI(), ePackage);
				registerInGlobalRegistry(EMFPackageRegistry, ePackage.getNsPrefix(), ePackage);
			}
			else {
				logger.warning("UML Profile already exists in registry : " + newMetaModelResource.getURI());
			}
		}
		else {
			// TODO : xtof, manage duplicate URIs
			logger.warning(" There already exists a MM with that URI => I will not register this one!");
		}
	}

	/** Accessors for Profile Collection */

	public Set<String> getAllProfileURIs() {
		return profiles.keySet();
	}

	public Collection<EMFMetaModelResource> getAllProfileResources() {
		return Collections.unmodifiableCollection(profiles.values());
	}

	public EMFMetaModelResource geProfileResourceByURI(String uri) {
		return profiles.get(uri);
	}

	/** Accessors for MetaModel Collection */

	public Set<String> getAllMetaModelURIs() {
		return metamodels.keySet();
	}

	public Collection<EMFMetaModelResource> getAllMetaModelResources() {
		return Collections.unmodifiableCollection(metamodels.values());
	}

	public EMFMetaModelResource getMetaModelResourceByURI(String uri) {
		return metamodels.get(uri);
	}

	// TODO: maybe it does not need/have to be a EMFMetaModelResource
	public EMFMetaModelResource getProfileResourceByURI(String uri) {
		return profiles.get(uri);
	}

	/**
	 * Called when a new model is registered, notify the {@link TechnologyContextManager}
	 * 
	 * @param newModel
	 */
	public void registerModel(EMFModelResource newModelResource) {
		registerResource(newModelResource);
		models.put(newModelResource.getURI(), newModelResource);
	}

	@Override
	public IndividualOfClass<EMFTechnologyAdapter, ?, ?> makeIndividualOfClass(IFlexoOntologyClass<EMFTechnologyAdapter> anOntologyClass) {
		if (anOntologyClass instanceof EMFClassClass) {
			return new EMFObjectIndividualType((EMFClassClass) anOntologyClass);
		}
		else if (anOntologyClass instanceof EMFEnumClass) {
			return new EMFEnumType((EMFEnumClass) anOntologyClass);
		}
		return null;
	}

	/**
	 * Put supplied value in supplied JVM-global registry, remembering what was there before, so that {@link #stop()} may restore the
	 * initial state
	 */
	private void registerInGlobalRegistry(Map<String, Object> globalRegistry, String key, Object value) {
		if (globalRegistry == null || key == null || value == null) {
			return;
		}
		globalRegistryEntries.add(new GlobalRegistryEntry(globalRegistry, key, value));
		globalRegistry.put(key, value);
	}

	/**
	 * Called when the {@link org.openflexo.foundation.technologyadapter.TechnologyAdapterService} is stopped.<br>
	 * 
	 * EMF registries (<code>EPackage.Registry.INSTANCE</code> and <code>Resource.Factory.Registry.INSTANCE</code>) are static, thus shared
	 * by every {@link org.openflexo.foundation.FlexoServiceManager} of the JVM, while the {@link EPackage} and {@link Resource.Factory} we
	 * register there for jar-based metamodels are instances coming from the class loaders we built (see
	 * {@link org.openflexo.technologyadapter.emf.rm.JarBasedMetaModelResourceImpl}). Leaving them behind makes the last stopped session win
	 * JVM-wide and retains every abandoned class loader, so this session releases them here: what we explicitly registered is restored to
	 * its previous value, and anything still pointing at classes of our own class loaders is removed — which also covers the entries the
	 * generated <code>&lt;Xxx&gt;PackageImpl.init()</code> silently self-registered under its nsURI.
	 */
	@Override
	public void stop() {

		for (int i = globalRegistryEntries.size() - 1; i >= 0; i--) {
			globalRegistryEntries.get(i).restore();
		}
		globalRegistryEntries.clear();

		Set<ClassLoader> ownClassLoaders = getOwnClassLoaders();
		if (!ownClassLoaders.isEmpty()) {
			removeEntriesLoadedBy(EMFPackageRegistry, ownClassLoaders);
			removeEntriesLoadedBy(EMFExtensionToFactoryMap, ownClassLoaders);
		}

		metamodels.clear();
		profiles.clear();
		models.clear();

		super.stop();
	}

	/**
	 * Return the class loaders which were built to load the metamodels of this {@link EMFTechnologyContextManager}, the application class
	 * loader being excluded: the metamodels it provides (Ecore, UML) are shared by the whole JVM and must survive this session
	 */
	private Set<ClassLoader> getOwnClassLoaders() {
		Set<ClassLoader> returned = new HashSet<>();
		List<EMFMetaModelResource> allMetaModelResources = new ArrayList<>(metamodels.values());
		allMetaModelResources.addAll(profiles.values());
		for (EMFMetaModelResource metaModelResource : allMetaModelResources) {
			EPackage ePackage = metaModelResource.getPackage();
			if (ePackage != null) {
				ClassLoader classLoader = ePackage.getClass().getClassLoader();
				if (classLoader != null && classLoader != getClass().getClassLoader()) {
					returned.add(classLoader);
				}
			}
		}
		return returned;
	}

	private static void removeEntriesLoadedBy(Map<String, Object> globalRegistry, Set<ClassLoader> classLoaders) {
		for (Iterator<Map.Entry<String, Object>> it = globalRegistry.entrySet().iterator(); it.hasNext();) {
			Object value = it.next().getValue();
			if (value != null && classLoaders.contains(value.getClass().getClassLoader())) {
				it.remove();
			}
		}
	}

	/**
	 * A value this {@link EMFTechnologyContextManager} put in a JVM-global EMF registry, and what that registry held for that key before
	 */
	private static class GlobalRegistryEntry {

		private final Map<String, Object> globalRegistry;
		private final String key;
		private final Object registeredValue;
		private final Object previousValue;
		private final boolean hadPreviousValue;

		GlobalRegistryEntry(Map<String, Object> globalRegistry, String key, Object registeredValue) {
			this.globalRegistry = globalRegistry;
			this.key = key;
			this.registeredValue = registeredValue;
			this.hadPreviousValue = globalRegistry.containsKey(key);
			this.previousValue = globalRegistry.get(key);
		}

		void restore() {
			if (globalRegistry.get(key) != registeredValue) {
				// Somebody else has overridden that entry in the meantime: it is not ours to restore anymore
				return;
			}
			if (hadPreviousValue) {
				globalRegistry.put(key, previousValue);
			}
			else {
				globalRegistry.remove(key);
			}
		}
	}

	public void newMetaModelWasRegistered(EMFMetaModelResource mmResource, FlexoResourceCenter<?> resourceCenter) {
		// We iterate on all EMFModelResource which does not declare any metamodel
		for (EMFModelResource emfModelResource : models.values()) {
			if (emfModelResource.getMetaModelResource() == null) {
				if (emfModelResource.getMetaModelResourceURI() != null
						&& emfModelResource.getMetaModelResourceURI().equals(mmResource.getURI())) {
					emfModelResource.setMetaModelResource(mmResource);
				}
			}
		}
	}

}
