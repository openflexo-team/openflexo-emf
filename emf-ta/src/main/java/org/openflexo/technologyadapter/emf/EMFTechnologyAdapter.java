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

import java.util.logging.Logger;

import org.eclipse.emf.ecore.impl.EcorePackageImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.internal.resource.UMLResourceFactoryImpl;
import org.openflexo.foundation.fml.annotations.DeclareModelSlots;
import org.openflexo.foundation.fml.annotations.DeclareResourceFactories;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterInitializationException;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.emf.fml.binding.EMFBindingFactory;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelRepository;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelResource;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelResourceFactory;
import org.openflexo.technologyadapter.emf.rm.EMFModelRepository;
import org.openflexo.technologyadapter.emf.rm.EMFModelResourceFactory;

/**
 * This class defines and implements the EMF technology adapter
 * 
 * @author sylvain
 * 
 */
@DeclareModelSlots({ EMFModelSlot.class, /*,EMFMetaModelSlot.class*/
		UMLEMFModelSlot.class })
@DeclareResourceFactories({ EMFMetaModelResourceFactory.class, EMFModelResourceFactory.class })
public class EMFTechnologyAdapter extends TechnologyAdapter<EMFTechnologyAdapter> {

	protected static final Logger logger = Logger.getLogger(EMFTechnologyAdapter.class.getPackage().getName());

	private static final EMFBindingFactory BINDING_FACTORY = new EMFBindingFactory();

	// Static references to ECORE properties

	public static String ECORE_MM_NAME = "Ecore Metamodel";
	public static String ECORE_MM_URI = "http://www.eclipse.org/emf/2002/Ecore";
	private static String ECORE_MM_EXT = "ecore";
	private static String ECORE_MM_PKGCLSNAME = EcorePackageImpl.class.getName();
	private static String ECORE_MM_FACTORYCLSNAME = EcoreResourceFactoryImpl.class.getName();
	private EMFMetaModelResource ecoreMetaModelResource = null;

	// Static references to UML properties

	public static String UML_MM_NAME = "UML Metamodel";
	public static String UML_MM_URI = "http://www.eclipse.org/uml2/5.0.0/UML";
	private static String UML_MM_EXT = "uml";
	private static String UML_MM_PKGCLSNAME = UMLPackage.class.getName();
	private static String UML_MM_FACTORYCLSNAME = UMLResourceFactoryImpl.class.getName();
	private EMFMetaModelResource umlMetaModelResource = null;

	/**
	 * 
	 * Constructor.
	 * 
	 * @throws TechnologyAdapterInitializationException
	 */
	public EMFTechnologyAdapter() throws TechnologyAdapterInitializationException {
	}

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#getName()
	 */
	@Override
	public String getName() {
		return "EMF technology adapter";
	}

	@Override
	protected String getLocalizationDirectory() {
		return "FlexoLocalization/EMFTechnologyAdapter";
	}

	@Override
	public void activate() {
		super.activate();
		registerClasspathMetaModels();
	}

	/**
	 * Initialize the supplied resource center with the technology<br>
	 * ResourceCenter is scanned, ResourceRepositories are created and new technology-specific resources are build and registered.
	 * 
	 * @param resourceCenter
	 */
	/*@Override
	public <I> void performInitializeResourceCenter(FlexoResourceCenter<I> resourceCenter) {
	
		EMFTechnologyContextManager technologyContextManager = (EMFTechnologyContextManager) getTechnologyAdapterService()
				.getTechnologyContextManager(this);
	
		// A single MM Repository for all ResourceCenters
	
		EMFMetaModelRepository mmRepository = resourceCenter.getRepository(EMFMetaModelRepository.class, this);
		if (mmRepository == null) {
			try {
				mmRepository = createMetaModelRepository(resourceCenter);
			} catch (ModelDefinitionException e) {
				e.printStackTrace();
			}
		}
	
		EMFModelRepository modelRepository = resourceCenter.getRepository(EMFModelRepository.class, this);
		if (modelRepository == null) {
			modelRepository = createModelRepository(resourceCenter);
		}
	
		// First pass on meta-models only
		Iterator<I> it = resourceCenter.iterator();
	
		while (it.hasNext()) {
			I item = it.next();
			if (item instanceof File) {
				File candidateFile = (File) item;
				EMFMetaModelResource mmRes = tryToLookupMetaModel(resourceCenter, candidateFile);
			}
		}
	
		// Second pass on models
		it = resourceCenter.iterator();
	
		while (it.hasNext()) {
			I item = it.next();
			if (item instanceof File) {
				File candidateFile = (File) item;
				EMFModelResource mRes = tryToLookupModel(resourceCenter, candidateFile);
			}
		}
	
		// Call it to update the current repositories
		notifyRepositoryStructureChanged();
	}*/

	/*protected EMFMetaModelResource tryToLookupMetaModel(FlexoResourceCenter<?> resourceCenter, File candidateFile) {
		EMFTechnologyContextManager technologyContextManager = getTechnologyContextManager();
		if (isValidMetaModelFile(candidateFile)) {
			EMFMetaModelResource mmRes = retrieveMetaModelResource(candidateFile, resourceCenter);
			EMFMetaModelRepository mmRepo = resourceCenter.getRepository(EMFMetaModelRepository.class, this);
			if (mmRes != null) {
				RepositoryFolder<EMFMetaModelResource> folder;
				try {
					folder = mmRepo.getRepositoryFolder(candidateFile, true);
					if (folder != null) {
						mmRepo.registerResource(mmRes, folder);
					}
					else
						mmRepo.registerResource(mmRes);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				referenceResource(mmRes, resourceCenter);
				return mmRes;
			}
		}
		return null;
	}*/

	/*protected EMFModelResource tryToLookupModel(FlexoResourceCenter<?> resourceCenter, File candidateFile) {
		EMFTechnologyContextManager technologyContextManager = getTechnologyContextManager();
		EMFMetaModelRepository mmRepository = resourceCenter.getRepository(EMFMetaModelRepository.class, this);
		EMFModelRepository modelRepository = resourceCenter.getRepository(EMFModelRepository.class, this);
	
		for (EMFMetaModelResource mmRes : technologyContextManager.getAllMetaModelResources()) {
			if (isValidModelFile(candidateFile, mmRes)) {
				EMFModelResource mRes = retrieveModelResource(candidateFile, mmRes, resourceCenter);
				if (mRes != null) {
					RepositoryFolder<EMFModelResource> folder;
					try {
						folder = modelRepository.getRepositoryFolder(candidateFile, true);
						modelRepository.registerResource(mRes, folder);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					referenceResource(mRes, resourceCenter);
					return mRes;
				}
			}
		}
		return null;
	}*/

	@Override
	public <I> boolean isIgnorable(FlexoResourceCenter<I> resourceCenter, I contents) {
		return false;
	}

	@Override
	protected <I> boolean isFolderIgnorable(FlexoResourceCenter<I> resourceCenter, I contents) {
		if (resourceCenter.isDirectory(contents)) {
			for (I c : resourceCenter.getContents(contents)) {
				if (resourceCenter.retrieveName(c).endsWith(EMFMetaModelResourceFactory.PROPERTIES_SUFFIX)) {
					return true;
				}
			}
		}
		return false;
	}

	/*@Override
	public <I> boolean contentsAdded(FlexoResourceCenter<I> resourceCenter, I contents) {
		if (contents instanceof File) {
			File candidateFile = (File) contents;
			if (tryToLookupMetaModel(resourceCenter, candidateFile) != null) {
				// This is a meta-model, this one has just been registered
				return true;
			}
			else {
				return (tryToLookupModel(resourceCenter, candidateFile) != null);
			}
		}
		return false;
	}
	
	@Override
	public <I> boolean contentsDeleted(FlexoResourceCenter<I> resourceCenter, I contents) {
		if (contents instanceof File) {
			System.out
					.println("File DELETED " + ((File) contents).getName() + " in " + ((File) contents).getParentFile().getAbsolutePath());
		}
		return false;
	}
	
	@Override
	public <I> boolean contentsModified(FlexoResourceCenter<I> resourceCenter, I contents) {
		return false;
	}
	
	@Override
	public <I> boolean contentsRenamed(FlexoResourceCenter<I> resourceCenter, I contents, String oldName, String newName) {
		return false;
	}*/

	/**
	 * 
	 * Create a metamodel repository for current {@link TechnologyAdapter} and supplied {@link FlexoResourceCenter}
	 * 
	 * @throws ModelDefinitionException
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#createMetaModelRepository(org.openflexo.foundation.resource.FlexoResourceCenter)
	 */
	/*public EMFMetaModelRepository createMetaModelRepository(FlexoResourceCenter<?> resourceCenter) throws ModelDefinitionException {
	
		EMFMetaModelRepository mmRepository = new EMFMetaModelRepository(this, resourceCenter);
	
		// Register Global MetaModels if ever we had a ResourceCenter configured
	
		registerClasspathMetaModels();
	
		resourceCenter.registerRepository(mmRepository, EMFMetaModelRepository.class, this);
	
		return mmRepository;
	}*/

	public <I> EMFMetaModelRepository<I> getEMFMetaModelRepository(FlexoResourceCenter<I> resourceCenter) {
		EMFMetaModelRepository<I> returned = resourceCenter.retrieveRepository(EMFMetaModelRepository.class, this);
		if (returned == null) {
			returned = EMFMetaModelRepository.instanciateNewRepository(this, resourceCenter);
			resourceCenter.registerRepository(returned, EMFMetaModelRepository.class, this);
		}
		return returned;
	}

	/**
	 * Registers the Metamodel that are provided by this technology adapter
	 */
	private void registerClasspathMetaModels() {

		ecoreMetaModelResource = getEMFMetaModelResourceFactory().retrieveResourceFromClassPath(ECORE_MM_NAME, ECORE_MM_URI, ECORE_MM_EXT,
				ECORE_MM_PKGCLSNAME, ECORE_MM_FACTORYCLSNAME, getTechnologyContextManager());

		umlMetaModelResource = getEMFMetaModelResourceFactory().retrieveResourceFromClassPath(UML_MM_NAME, UML_MM_URI, UML_MM_EXT,
				UML_MM_PKGCLSNAME, UML_MM_FACTORYCLSNAME, getTechnologyContextManager());

		/*ModelFactory factory = new ModelFactory(ModelContextLibrary.getCompoundModelContext(FileFlexoIODelegate.class,
				MMFromClasspathIODelegate.class, EMFMetaModelResource.class, XtextEMFMetaModelResource.class));
		
		if (ecoreMetaModelResource == null) {
			// register ecore MM once for all resource centers
			MMFromClasspathIODelegate iodelegate = MMFromClasspathIODelegateImpl.makeMMFromClasspathIODelegate(factory);
			ecoreMetaModelResource = factory.newInstance(EMFMetaModelResource.class);
			ecoreMetaModelResource.setFlexoIODelegate(iodelegate);
			iodelegate.setFlexoResource(ecoreMetaModelResource);
			ecoreMetaModelResource.setTechnologyAdapter(this);
			ecoreMetaModelResource.setURI(ECORE_MM_URI);
			ecoreMetaModelResource.initName(ECORE_MM_NAME);
			ecoreMetaModelResource.setModelFileExtension(ECORE_MM_EXT);
			ecoreMetaModelResource.setPackageClassName(ECORE_MM_PKGCLSNAME);
			ecoreMetaModelResource.setResourceFactoryClassName(ECORE_MM_FACTORYCLSNAME);
			// This resource has no resource center because it will never been serialized
			ecoreMetaModelResource.setServiceManager(getTechnologyAdapterService().getServiceManager());
			getTechnologyContextManager().registerMetaModel(ecoreMetaModelResource);
		}
		
		if (umlMetaModelResource == null) {
			// register ecore MM once for all resource centers
			MMFromClasspathIODelegate iodelegate = MMFromClasspathIODelegateImpl.makeMMFromClasspathIODelegate(factory);
			umlMetaModelResource = factory.newInstance(EMFMetaModelResource.class);
			umlMetaModelResource.setFlexoIODelegate(iodelegate);
			iodelegate.setFlexoResource(umlMetaModelResource);
			umlMetaModelResource.setTechnologyAdapter(this);
			umlMetaModelResource.setURI(UML_MM_URI);
			umlMetaModelResource.initName(UML_MM_NAME);
			umlMetaModelResource.setModelFileExtension(UML_MM_EXT);
			umlMetaModelResource.setPackageClassName(UML_MM_PKGCLSNAME);
			umlMetaModelResource.setResourceFactoryClassName(UML_MM_FACTORYCLSNAME);
			// This resource has no resource center because it will never been serialized
			umlMetaModelResource.setServiceManager(getTechnologyAdapterService().getServiceManager());
			getTechnologyContextManager().registerMetaModel(umlMetaModelResource);
		}*/

	}

	public <I> EMFModelRepository<I> getEMFModelRepository(FlexoResourceCenter<I> resourceCenter) {
		EMFModelRepository<I> returned = resourceCenter.retrieveRepository(EMFModelRepository.class, this);
		if (returned == null) {
			returned = EMFModelRepository.instanciateNewRepository(this, resourceCenter);
			resourceCenter.registerRepository(returned, EMFModelRepository.class, this);
		}
		return returned;
	}

	/**
	 * Return flag indicating if supplied file represents a valid metamodel
	 * 
	 * @param aMetaModelFile
	 * @param technologyContextManager
	 * 
	 * @return
	 */
	/*public boolean isValidMetaModelFile(File aMetaModelFile) {
		return MMFromJarsInDirIODelegateImpl.isValidMetaModelFile(aMetaModelFile);
	*/

	/**
	 * 
	 * Instantiate new meta model resource stored in supplied meta model file
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#retrieveMetaModelResource(java.io.File,
	 *      org.openflexo.foundation.resource.FlexoResourceCenter)
	 */
	/*public EMFMetaModelResource retrieveMetaModelResource(final File aMetaModelFile, FlexoResourceCenter<?> resourceCenter) {
	
		EMFMetaModelResource metaModelResource = null;
	
		ModelFactory factory;
		try {
	
			factory = new ModelFactory(ModelContextLibrary.getCompoundModelContext(MMFromJarsInDirIODelegate.class,
					EMFMetaModelResource.class, XtextEMFMetaModelResource.class));
	
			MMFromJarsInDirIODelegate iodelegate = MMFromJarsInDirIODelegateImpl.makeMMFromJarsInDirIODelegate(aMetaModelFile, factory);
	
			metaModelResource = iodelegate.retrieveMetaModelResource(aMetaModelFile, factory, this.getTechnologyContextManager());
	
			metaModelResource.setTechnologyAdapter(this);
			metaModelResource.setFlexoIODelegate(iodelegate);
			metaModelResource.setResourceCenter(resourceCenter);
			metaModelResource.setServiceManager(getTechnologyAdapterService().getServiceManager());
	
			return metaModelResource;
	
		} catch (ModelDefinitionException e) {
			logger.warning("Unable to Create EMF Metamodel from directory: " + aMetaModelFile);
			e.printStackTrace();
		}
	
		return null;
	
	}*/

	/**
	 * Return flag indicating if supplied file represents a valid model conform to supplied meta-model
	 * 
	 * @param aModelFile
	 * @param metaModelResource
	 * @param technologyContextManager
	 * @return
	 */
	/*public boolean isValidModelFile(File aModelFile, EMFMetaModelResource metaModelResource) {
		boolean valid = false;
		if (aModelFile.exists()) {
			// TODO syntaxic check and conformity to XMI
			if (aModelFile.getName().endsWith("." + metaModelResource.getModelFileExtension())) {
				if (aModelFile.isFile()) {
					valid = true;
				}
			}
		}
		return valid;
	}*/

	/**
	 * Retrieve and return URI for supplied model file
	 * 
	 * @param aModelFile
	 * @param technologyContextManager
	 * @return
	 */
	/*public String retrieveModelURI(File aModelFile, EMFMetaModelResource metaModelResource, FlexoResourceCenter<?> resourceCenter) {
		return retrieveModelResource(aModelFile, metaModelResource, resourceCenter).getURI();
	}*/

	/**
	 * Instantiate new model resource stored in supplied model file, given the conformant metamodel<br>
	 * We assert here that model resource is conform to supplied metamodel, ie we will not try to lookup the metamodel but take the one
	 * which was supplied
	 * 
	 */
	/*public EMFModelResource retrieveModelResource(File aModelFile, FlexoResource<EMFMetaModel> metaModelResource,
			FlexoResourceCenter<?> resourceCenter) {
		EMFModelResource emfModelResource = null;
	
		emfModelResource = getTechnologyContextManager().getModel(aModelFile);
	
		if (emfModelResource == null) {
			// TODO refactor with ioDelegates
			emfModelResource = EMFModelResourceImpl.retrieveEMFModelResource(aModelFile, (EMFMetaModelResource) metaModelResource,
					getTechnologyContextManager(), resourceCenter);
		}
	
		return emfModelResource;
	}*/

	/**
	 * Create empty EMFModel.
	 * 
	 * @param project
	 * @param filename
	 * @param modelUri
	 * @param metaModelResource
	 * @param technologyContextManager
	 * @return
	 */
	/*public EMFModelResource createNewEMFModel(FlexoResourceCenter<?> rc, String filename, String modelUri,
			EMFMetaModelResource metaModelResource) {
		if (rc instanceof FlexoProject) {
			File modelFile = new File(FlexoProject.getProjectSpecificModelsDirectory((FlexoProject) rc), filename);
			return createNewEMFModel(modelFile, modelUri, metaModelResource, rc);
		}
		else {
			logger.warning("INVESTIGATE: not implemented yet, cannot create an EMFModel in a non FlexoProject" + rc.toString());
			return null;
		}
	}*/

	/**
	 * Create empty EMFModel.
	 * 
	 * @param modelFilePath
	 * @param metaModelResource
	 * @param technologyContextManager
	 * @return
	 */
	/*public EMFModelResource createNewEMFModel(FileSystemBasedResourceCenter resourceCenter, String relativePath, String filename,
			String modelUri, EMFMetaModelResource metaModelResource) {
		File modelDirectory = new File(resourceCenter.getRootDirectory(), relativePath);
		File modelFile = new File(modelDirectory, filename);
		return createNewEMFModel(modelFile, modelUri, metaModelResource, resourceCenter);
	}*/

	/**
	 * Create empty EMFModel.
	 * 
	 * @param modelFile
	 * @param modelUri
	 * @param metaModelResource
	 * @param technologyContextManager
	 * @return
	 */
	/*public EMFModelResource createNewEMFModel(File modelFile, String modelUri, EMFMetaModelResource metaModelResource,
			FlexoResourceCenter<?> resourceCenter) {
		EMFMetaModelResource emfMetaModelResource = metaModelResource;
		EMFModelResource emfModelResource = EMFModelResourceImpl.makeEMFModelResource(modelUri, modelFile, emfMetaModelResource,
				getTechnologyContextManager(), resourceCenter);
		getTechnologyContextManager().registerModel(emfModelResource);
		try {
			emfModelResource.save(null);
		} catch (SaveResourceException e) {
			e.printStackTrace();
		}
		System.out.println(
				"Created empty model " + modelFile.getAbsolutePath() + " as " + modelUri + " conform to " + metaModelResource.getURI());
		return emfModelResource;
	}*/

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#createNewModelSlot(org.openflexo.foundation.fml.ViewPoint)
	 */
	@Override
	public EMFTechnologyContextManager createTechnologyContextManager(FlexoResourceCenterService service) {
		return new EMFTechnologyContextManager(this, service);
	}

	@Override
	public EMFTechnologyContextManager getTechnologyContextManager() {
		return (EMFTechnologyContextManager) super.getTechnologyContextManager();
	}

	@Override
	public EMFBindingFactory getTechnologyAdapterBindingFactory() {
		return BINDING_FACTORY;
	}

	public String getExpectedModelExtension(EMFMetaModelResource metaModelResource) {
		if (metaModelResource != null) {
			return "." + metaModelResource.getModelFileExtension();
		}
		return null;
	}

	@Override
	public String getIdentifier() {
		return "EMF";
	}

	public EMFModelResourceFactory getEMFModelResourceFactory() {
		return getResourceFactory(EMFModelResourceFactory.class);
	}

	public EMFMetaModelResourceFactory getEMFMetaModelResourceFactory() {
		return getResourceFactory(EMFMetaModelResourceFactory.class);
	}

	/*@Override
	protected <I> void foundFolder(FlexoResourceCenter<I> resourceCenter, I folder) throws IOException {
		super.foundFolder(resourceCenter, folder);
		if (resourceCenter.isDirectory(folder)) {
			getEMFModelRepository(resourceCenter).getRepositoryFolder(folder, true);
			getEMFModelRepository(resourceCenter).getRepositoryFolder(folder, true);
		}
	}*/

}
