/**
 * 
 * Copyright (c) 2015-2015, Openflexo
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

package org.openflexo.technologyadapter.emf.gui.browser;

import static org.junit.Assert.assertNotNull;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.gina.test.OpenflexoTestCaseWithGUI;
import org.openflexo.gina.test.SwingGraphicalContextDelegate;
import org.openflexo.gina.view.widget.browser.impl.FIBBrowserModel;
import org.openflexo.ontology.components.widget.OntologyBrowserModel;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.gui.EMFModelBrowserModel;
import org.openflexo.technologyadapter.emf.gui.EMFModelView;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelRepository;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelResource;
import org.openflexo.technologyadapter.emf.rm.EMFModelRepository;
import org.openflexo.technologyadapter.emf.rm.EMFModelResource;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;
import org.openflexo.test.UITest;

/**
 * Test Class for OntologyBrowser on an UMLModel
 * 
 * @author xtof
 * 
 */
@RunWith(OrderedRunner.class)
public class TestCity1BrowerModel extends OpenflexoTestCaseWithGUI {
	protected static final Logger logger = Logger.getLogger(TestCity1BrowerModel.class.getPackage().getName());

	static EMFTechnologyAdapter technologicalAdapter;
	static EMFModelResource city1ModelResource = null;
	static EMFModel city1Model = null;

	private static String CITY1_MM_URI = "http://www.thalesgroup.com/openflexo/emf/model/city1";

	private static SwingGraphicalContextDelegate gcDelegate;

	static String city1ModelResourceRelativeURI = "TestResourceCenter/EMF/Model/city1/first.city1";

	@BeforeClass
	public static void setupClass() {
		instanciateTestServiceManager(EMFTechnologyAdapter.class);

		technologicalAdapter = serviceManager.getTechnologyAdapterService().getTechnologyAdapter(EMFTechnologyAdapter.class);

		initGUI();

		// Default behaviour is to update browser cells asynchronously in event-dispatch-thread
		// But in this test environment, we need to "force" the update to be done synchronously
		FIBBrowserModel.UPDATE_BROWSER_SYNCHRONOUSLY = true;

	}

	@Test
	@TestOrder(1)
	@Category(UITest.class)
	public void TestLoadCity1EMFMetaModel() {

		EMFTechnologyAdapter technologicalAdapter = serviceManager.getTechnologyAdapterService()
				.getTechnologyAdapter(EMFTechnologyAdapter.class);

		for (FlexoResourceCenter<?> resourceCenter : serviceManager.getResourceCenterService().getResourceCenters()) {
			EMFMetaModelRepository<?> metaModelRepository = technologicalAdapter.getEMFMetaModelRepository(resourceCenter);
			assertNotNull(metaModelRepository);

			EMFMetaModelResource metaModelResource = metaModelRepository.getResource(CITY1_MM_URI);

			if (metaModelResource != null) {

				System.out.println("\t Loading " + metaModelResource.getURI());

				EMFMetaModel metamodel = metaModelResource.getMetaModelData();

				assertNotNull(metamodel);
			}
		}
	}

	@Test
	@TestOrder(2)
	@Category(UITest.class)
	public void TestLoadCity1EMFModel() {

		for (FlexoResourceCenter<?> resourceCenter : serviceManager.getResourceCenterService().getResourceCenters()) {

			EMFMetaModelRepository<?> metaModelRepository = technologicalAdapter.getEMFMetaModelRepository(resourceCenter);
			assertNotNull(metaModelRepository);
			EMFModelRepository<?> modelRepository = technologicalAdapter.getEMFModelRepository(resourceCenter);
			assertNotNull(modelRepository);

			System.out.println(
					"Loading from RC :" + resourceCenter + " " + resourceCenter.getDefaultBaseURI() + "/" + city1ModelResourceRelativeURI);

			EMFModelResource modelResource = modelRepository
					.getResource(resourceCenter.getDefaultBaseURI() + "/" + city1ModelResourceRelativeURI);

			if (modelResource != null) {
				city1ModelResource = modelResource;
				System.out.println("Found resource " + resourceCenter.getDefaultBaseURI() + "/" + city1ModelResourceRelativeURI);
			}
			else {
				System.out.println("Not found: " + resourceCenter.getDefaultBaseURI() + "/" + city1ModelResourceRelativeURI);
				for (FlexoResource<?> r : resourceCenter.getAllResources()) {
					System.out.println(" > " + r.getURI());
				}
			}

		}

		assertNotNull(city1ModelResource);

		city1Model = city1ModelResource.getModel();
		assertNotNull(city1Model);
		assertNotNull(city1Model.getMetaModel());
	}

	@Test
	@TestOrder(3)
	@Category(UITest.class)
	public void TestCreateOntologyBrowser() {

		long previousDate, currentDate;

		long startTime = System.currentTimeMillis();

		OntologyBrowserModel<EMFTechnologyAdapter> obm = new EMFModelBrowserModel(city1Model);

		long endTime = System.currentTimeMillis();

		System.out.println("\t\t Building OntologyBrowser took " + (endTime - startTime) + " milliseconds");

		obm.setStrictMode(true);
		obm.setHierarchicalMode(false);

		startTime = System.currentTimeMillis();

		obm.recomputeStructure();

		endTime = System.currentTimeMillis();

		System.out.println("\t\t Recomputing OntologyBrowser took  " + (endTime - startTime) + " milliseconds");

		previousDate = System.currentTimeMillis();

		obm.setShowIndividuals(false);
		currentDate = System.currentTimeMillis();
		System.out.println(" setShowIndividuals took: " + (currentDate - previousDate));
		previousDate = currentDate;

		obm.setShowClasses(false);
		currentDate = System.currentTimeMillis();
		System.out.println(" setShowClasses took: " + (currentDate - previousDate));
		previousDate = currentDate;

		obm.setShowDataProperties(false);
		currentDate = System.currentTimeMillis();
		System.out.println(" setShowDataProperties took: " + (currentDate - previousDate));
		previousDate = currentDate;

		obm.setShowObjectProperties(false);
		currentDate = System.currentTimeMillis();
		System.out.println(" setShowObjectProperties took: " + (currentDate - previousDate));
		previousDate = currentDate;

		obm.setShowAnnotationProperties(false);
		currentDate = System.currentTimeMillis();
		System.out.println(" setShowAnnotationProperties took: " + (currentDate - previousDate));
		previousDate = currentDate;

		obm.recomputeStructure();
		currentDate = System.currentTimeMillis();
		System.out.println(" recomputeStructure took: " + (currentDate - previousDate));
		previousDate = currentDate;

		obm.setShowClasses(true);
		currentDate = System.currentTimeMillis();
		System.out.println(" setShowClasses took: " + (currentDate - previousDate));
		previousDate = currentDate;

		obm.setShowDataProperties(true);
		currentDate = System.currentTimeMillis();
		System.out.println(" setShowDataProperties took: " + (currentDate - previousDate));
		previousDate = currentDate;

		obm.setShowObjectProperties(true);
		currentDate = System.currentTimeMillis();
		System.out.println(" setShowObjectProperties took: " + (currentDate - previousDate));
		previousDate = currentDate;

		obm.setShowAnnotationProperties(true);
		currentDate = System.currentTimeMillis();
		System.out.println(" setShowAnnotationProperties took: " + (currentDate - previousDate));
		previousDate = currentDate;

		obm.setShowIndividuals(true);
		currentDate = System.currentTimeMillis();
		System.out.println(" setShowIndividuals took: " + (currentDate - previousDate));
		previousDate = currentDate;

		obm.recomputeStructure();
		currentDate = System.currentTimeMillis();
		System.out.println(" recomputeStructure took: " + (currentDate - previousDate));
		previousDate = currentDate;

		obm.setShowIndividuals(false);
		currentDate = System.currentTimeMillis();
		System.out.println(" setShowIndividuals took: " + (currentDate - previousDate));
		previousDate = currentDate;

		System.out.println(" setShowClasses took: " + (currentDate - previousDate));
		previousDate = currentDate;

		obm.setShowDataProperties(false);
		currentDate = System.currentTimeMillis();
		System.out.println(" setShowDataProperties took: " + (currentDate - previousDate));
		previousDate = currentDate;

		obm.setShowObjectProperties(false);
		currentDate = System.currentTimeMillis();
		System.out.println(" setShowObjectProperties took: " + (currentDate - previousDate));
		previousDate = currentDate;

		obm.setShowAnnotationProperties(false);
		currentDate = System.currentTimeMillis();
		System.out.println(" setShowAnnotationProperties took: " + (currentDate - previousDate));
		previousDate = currentDate;

		obm.recomputeStructure();
		currentDate = System.currentTimeMillis();
		System.out.println(" recomputeStructure took: " + (currentDate - previousDate));
		previousDate = currentDate;
	}

	@Test
	@TestOrder(4)
	@Category(UITest.class)
	public void TestCreateEMFModelView() {
		long previousDate, currentDate;

		logger.info("TestCreateEMFModelView");

		previousDate = System.currentTimeMillis();

		EMFModelView modelView = new EMFModelView(city1Model, null, null);
		currentDate = System.currentTimeMillis();
		System.out.println(" initial creation of view took : " + (currentDate - previousDate));
		previousDate = currentDate;

		gcDelegate.addTab("City1", modelView.getFIBController());

		previousDate = System.currentTimeMillis();

		int i = 2;

		while (i > 0) {
			i--;
			modelView.setShowIndividuals(false);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowIndividuals (FALSE)  took: " + (currentDate - previousDate));
			previousDate = currentDate;

			modelView.setShowClasses(false);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowClasses (FALSE)  took: " + (currentDate - previousDate));
			previousDate = currentDate;

			modelView.setShowDataProperties(false);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowDataProperties (FALSE)  took: " + (currentDate - previousDate));
			previousDate = currentDate;

			modelView.setShowObjectProperties(false);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowObjectProperties (FALSE)  took: " + (currentDate - previousDate));
			previousDate = currentDate;

			modelView.setShowAnnotationProperties(false);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowAnnotationProperties (FALSE)  took: " + (currentDate - previousDate));
			previousDate = currentDate;

			modelView.update();
			currentDate = System.currentTimeMillis();
			System.out.println(" update   took: " + (currentDate - previousDate));
			previousDate = currentDate;

			modelView.setShowClasses(true);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowClasses (TRUE)  took: " + (currentDate - previousDate));
			previousDate = currentDate;

			modelView.setShowDataProperties(true);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowDataProperties (TRUE) took: " + (currentDate - previousDate));
			previousDate = currentDate;

			modelView.setShowObjectProperties(true);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowObjectProperties (TRUE)  took: " + (currentDate - previousDate));
			previousDate = currentDate;

			modelView.setShowAnnotationProperties(true);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowAnnotationProperties (TRUE) took: " + (currentDate - previousDate));
			previousDate = currentDate;

			modelView.setShowIndividuals(true);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowIndividuals (TRUE) took: " + (currentDate - previousDate));
			previousDate = currentDate;

			modelView.update();
			currentDate = System.currentTimeMillis();
			System.out.println(" update  took: " + (currentDate - previousDate));
			previousDate = currentDate;

			int mb = 1024 * 1024;

			// Getting the runtime reference from system
			Runtime runtime = Runtime.getRuntime();

			System.out.println("##### Heap utilization statistics [MB] #####");

			// Print used memory
			System.out.println("Used Memory:" + (runtime.totalMemory() - runtime.freeMemory()) / mb);

			// Print free memory
			System.out.println("Free Memory:" + runtime.freeMemory() / mb);

			// Print total available memory
			System.out.println("Total Memory:" + runtime.totalMemory() / mb);

			// Print Maximum available memory
			System.out.println("Max Memory:" + runtime.maxMemory() / mb);

		}
	}

	public static void initGUI() {
		gcDelegate = new SwingGraphicalContextDelegate(TestCity1BrowerModel.class.getSimpleName());
	}

	@AfterClass
	public static void waitGUI() {
		gcDelegate.waitGUI();
	}

	@Before
	public void setUp() {
		gcDelegate.setUp();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		gcDelegate.tearDown();
	}
}
