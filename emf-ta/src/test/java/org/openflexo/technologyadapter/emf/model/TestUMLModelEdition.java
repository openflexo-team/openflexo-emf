/**
 * 
 * Copyright (c) 2014-2015, Openflexo
 * Copyright (c) 2012, THALES SYSTEMES AEROPORTES - All Rights Reserved
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

package org.openflexo.technologyadapter.emf.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.fml.CreationScheme;
import org.openflexo.foundation.fml.FMLTechnologyAdapter;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.action.AddUseDeclaration;
import org.openflexo.foundation.fml.action.CreateFlexoBehaviour;
import org.openflexo.foundation.fml.action.CreateFlexoConcept;
import org.openflexo.foundation.fml.rm.VirtualModelResource;
import org.openflexo.foundation.fml.rm.VirtualModelResourceFactory;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.foundation.fml.rt.action.CreateBasicVirtualModelInstance;
import org.openflexo.foundation.fml.rt.action.CreationSchemeAction;
import org.openflexo.foundation.resource.DirectoryResourceCenter;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.test.OpenflexoProjectAtRunTimeTestCase;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.emf.EMFModelSlot;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.UMLEMFModelSlot;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelRepository;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelResource;
import org.openflexo.technologyadapter.emf.rm.EMFModelResource;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Test Class for EMF Model Edition.
 * 
 * @author gbesancon, xtof
 * 
 */
@RunWith(OrderedRunner.class)
public class TestUMLModelEdition extends OpenflexoProjectAtRunTimeTestCase {
	protected static final Logger logger = Logger.getLogger(TestEMFModelEdition.class.getPackage().getName());

	public static final String VIEWPOINT_NAME = "TestViewPoint";
	public static final String VIEWPOINT_URI = "http://openflexo.org/test/TestViewPoint";
	public static final String VIRTUAL_MODEL_NAME = "TestVirtualModel";

	static FlexoEditor editor;
	static EMFTechnologyAdapter technologicalAdapter;
	static VirtualModel newViewPoint;
	static VirtualModel newVirtualModel;
	static EMFModelSlot newModelSlot = null;
	static EMFModelResource umlModelResource = null;
	static EMFMetaModelResource umlMetaModelResource = null;
	private static FlexoProject<File> project;
	private static FMLRTVirtualModelInstance newView;
	private static FMLRTVirtualModelInstance newVirtualModelInstance;
	private static FlexoConcept flexoConcept;
	private static CreateFlexoBehaviour creationEditionScheme;
	private static CreationScheme creationScheme;
	private static CreationSchemeAction creationSchemeCreationAction;

	private static DirectoryResourceCenter newResourceCenter;
	private static FlexoResourceCenter<?> emfResourceCenter;

	/**
	 * Test the VP creation
	 * 
	 * @throws ModelDefinitionException
	 * @throws SaveResourceException
	 * @throws IOException
	 */
	@Test
	@TestOrder(1)
	public void testCreateViewPoint() throws SaveResourceException, ModelDefinitionException, IOException {
		instanciateTestServiceManager(EMFTechnologyAdapter.class);

		newResourceCenter = makeNewDirectoryResourceCenter();

		technologicalAdapter = serviceManager.getTechnologyAdapterService().getTechnologyAdapter(EMFTechnologyAdapter.class);

		FMLTechnologyAdapter fmlTechnologyAdapter = serviceManager.getTechnologyAdapterService()
				.getTechnologyAdapter(FMLTechnologyAdapter.class);
		VirtualModelResourceFactory factory = fmlTechnologyAdapter.getVirtualModelResourceFactory();

		VirtualModelResource newViewPointResource = factory.makeTopLevelVirtualModelResource(VIEWPOINT_NAME, VIEWPOINT_URI,
				fmlTechnologyAdapter.getGlobalRepository(newResourceCenter).getRootFolder(), true);
		newViewPoint = newViewPointResource.getLoadedResourceData();

		assertTrue(((VirtualModelResource) newViewPoint.getResource()).getDirectory() != null);
		assertTrue(((VirtualModelResource) newViewPoint.getResource()).getIODelegate().exists());
	}

	/**
	 * Test the VirtualModel creation
	 * 
	 * @throws ModelDefinitionException
	 */
	@Test
	@TestOrder(2)
	public void testCreateVirtualModel() throws SaveResourceException, ModelDefinitionException {

		emfResourceCenter = serviceManager.getResourceCenterService().getFlexoResourceCenter("http://openflexo.org/emf-test");
		assertNotNull(emfResourceCenter);

		EMFMetaModelRepository<?> emfMetaModelRepository = technologicalAdapter.getEMFMetaModelRepository(emfResourceCenter);

		umlMetaModelResource = technologicalAdapter.getTechnologyContextManager()
				.getMetaModelResourceByURI(EMFTechnologyAdapter.UML_MM_URI);

		assertNotNull(umlMetaModelResource);

		FMLTechnologyAdapter fmlTechnologyAdapter = serviceManager.getTechnologyAdapterService()
				.getTechnologyAdapter(FMLTechnologyAdapter.class);
		VirtualModelResourceFactory factory = fmlTechnologyAdapter.getVirtualModelResourceFactory();
		VirtualModelResource newVMResource = factory.makeContainedVirtualModelResource(VIRTUAL_MODEL_NAME,
				newViewPoint.getVirtualModelResource(), true);
		newVirtualModel = newVMResource.getLoadedResourceData();

		assertTrue(((VirtualModelResource) newViewPoint.getResource()).getDirectory() != null);
		assertTrue(((VirtualModelResource) newViewPoint.getResource()).getIODelegate().exists());

		AddUseDeclaration useDeclarationAction = AddUseDeclaration.actionType.makeNewAction(newVirtualModel, null, _editor);
		useDeclarationAction.setModelSlotClass(UMLEMFModelSlot.class);
		useDeclarationAction.doAction();

		newModelSlot = technologicalAdapter.makeModelSlot(UMLEMFModelSlot.class, newVirtualModel);
		newModelSlot.setName("umlModel");
		newModelSlot.setMetaModelResource(umlMetaModelResource);
		assertNotNull(newModelSlot);
		newVirtualModel.addToModelSlots(newModelSlot);
		assertTrue(newVirtualModel.getModelSlots(UMLEMFModelSlot.class).size() == 1);
	}

	@Test
	@TestOrder(3)
	public void testCreateProject() {
		editor = createStandaloneProject("TestProject");
		project = (FlexoProject<File>) editor.getProject();
		System.out.println("Created project " + project.getProjectDirectory());
		assertTrue(project.getProjectDirectory().exists());
	}

	@Test
	@TestOrder(4)
	public void testCreateEMFModel() throws ModelDefinitionException {
		try {

			assertNotNull(umlMetaModelResource);

			RepositoryFolder<FlexoResource<?>, File> modelFolder = project.createNewFolder("Models");
			File serializationArtefact = new File(modelFolder.getSerializationArtefact(), "coucou.uml");
			umlModelResource = technologicalAdapter.getEMFModelResourceFactory().makeEMFModelResource(serializationArtefact,
					umlMetaModelResource, newResourceCenter, "coucou.uml", "myURI", true);
			/*
			 * try { RepositoryFolder<FlexoResource<?>> modelFolder =
			 * project.createNewFolder("Models"); umlModelResource =
			 * technologicalAdapter.createNewEMFModel(new
			 * File(modelFolder.getFile(), "coucou.uml"), "myURI",
			 * umlMetaModelResource, resourceCenter); } catch (Exception e) {
			 * e.printStackTrace(); }
			 */

			assertNotNull(umlModelResource);

			umlModelResource.save();

		} catch (SaveResourceException e) {
			e.printStackTrace();
		}
	}

	@Test
	@TestOrder(5)
	public void testCreateVMI() {

		CreateBasicVirtualModelInstance viewAction = CreateBasicVirtualModelInstance.actionType
				.makeNewAction(project.getVirtualModelInstanceRepository().getRootFolder(), null, editor);
		viewAction.setNewVirtualModelInstanceName("MyView");
		viewAction.setNewVirtualModelInstanceTitle("Test creation of a new view");
		viewAction.setVirtualModel(newViewPoint);
		viewAction.doAction();
		assertTrue(viewAction.hasActionExecutionSucceeded());
		newView = viewAction.getNewVirtualModelInstance();

		CreateBasicVirtualModelInstance vmiAction = CreateBasicVirtualModelInstance.actionType.makeNewAction(newView, null, editor);
		vmiAction.setNewVirtualModelInstanceName("MyVMI");
		vmiAction.setVirtualModel(newVirtualModel);
		vmiAction.setNewVirtualModelInstanceTitle("My Virtual Model Instance");

		logger.info("Creating a new FMLRTVirtualModelInstance");
		vmiAction.doAction();
		newVirtualModelInstance = vmiAction.getNewVirtualModelInstance();

		newVirtualModelInstance.setFlexoPropertyValue(newModelSlot, umlModelResource.getLoadedResourceData());

	}

	@Test
	@TestOrder(6)
	public void testCreateFlexoConceptC() throws SaveResourceException {

		CreateFlexoConcept addEP = CreateFlexoConcept.actionType.makeNewAction(newVirtualModel, null, editor);
		addEP.setNewFlexoConceptName("EMFFlexoConcept");
		addEP.doAction();

		flexoConcept = addEP.getNewFlexoConcept();

		System.out.println("FlexoConcept = " + flexoConcept);
		assertNotNull(flexoConcept);

		creationEditionScheme = CreateFlexoBehaviour.actionType.makeNewAction(flexoConcept, null, editor);
		creationEditionScheme.setFlexoBehaviourClass(CreationScheme.class);
		creationEditionScheme.setFlexoBehaviourName("DynamicCreation");
		assertNotNull(creationEditionScheme);
		creationEditionScheme.doAction();

		((VirtualModelResource) newVirtualModel.getResource()).save();

		System.out.println("Saved: " + ((VirtualModelResource) newVirtualModel.getResource()).getIODelegate().toString());

		/**
		 * NamedElement e = null; Profile profile = null; profile.getAllProfileApplications(); e.getApplicableStereotypes();
		 **/

	}

	@Test
	@TestOrder(7)
	public void testEditEMFModelinVMI() {

		try {

			creationScheme = (CreationScheme) creationEditionScheme.getNewFlexoBehaviour();
			assertNotNull(creationScheme);

			creationSchemeCreationAction = new CreationSchemeAction(creationScheme, newVirtualModelInstance, null, editor);
			assertNotNull(creationSchemeCreationAction);

			/* TODO some stuff TO DO HERE */

			umlModelResource.save();

		} catch (FlexoException e) {
			e.printStackTrace();
		}

	}
}
