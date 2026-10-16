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

import static org.junit.Assert.assertEquals;
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
import org.openflexo.ontology.components.widget.OntologyBrowserModel;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.gui.EMFMetaModelBrowserModel;
import org.openflexo.technologyadapter.emf.gui.EMFMetaModelView;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelRepository;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelResource;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;
import org.openflexo.test.UITest;

/**
 * Test Class for OntologyBrowser on an ECore MetaModel
 *
 * A <code>.ecore</code> artefact is interpreted as an {@link EMFMetaModel} (and never as a model conform to the ECore meta-meta-model), it
 * is thus retrieved from the {@link EMFMetaModelRepository}, using the URI declared by the ECore package itself
 *
 * @author xtof
 *
 */
@RunWith(OrderedRunner.class)
public class TestEcoreOntologyBrowerModel extends OpenflexoTestCaseWithGUI {
	protected static final Logger logger = Logger.getLogger(TestEcoreOntologyBrowerModel.class.getPackage().getName());

	static EMFTechnologyAdapter technologicalAdapter;
	static EMFMetaModelResource ecoreMetaModelResource = null;
	static EMFMetaModel ecoreMetaModel = null;

	private static SwingGraphicalContextDelegate gcDelegate;

	/** URI of the ECore package serialized in TestResourceCenter/EMF/Ecore/example.ecore */
	static String ecoreMetaModelURI = "http://www.itemis.de/showcases/cdo/market/0.0.1";

	@BeforeClass
	public static void setupClass() {
		instanciateTestServiceManager(EMFTechnologyAdapter.class);

		technologicalAdapter = serviceManager.getTechnologyAdapterService().getTechnologyAdapter(EMFTechnologyAdapter.class);

		initGUI();

		// Default behaviour is to update browser cells asynchronously in event-dispatch-thread
		// But in this test environment, we need to "force" the update to be done synchronously
		// FIBBrowserModel.UPDATE_BROWSER_SYNCHRONOUSLY = true;

		// We don't do it synchronously because it takes too much time
		// I don't have time yet to investigate on this
	}

	@Test
	@TestOrder(1)
	@Category(UITest.class)
	public void TestLoadECOREMetaModel() {
		for (FlexoResourceCenter<?> resourceCenter : serviceManager.getResourceCenterService().getResourceCenters()) {

			EMFMetaModelRepository<?> metaModelRepository = technologicalAdapter.getEMFMetaModelRepository(resourceCenter);
			assertNotNull(metaModelRepository);

			System.out.println("Loading :" + ecoreMetaModelURI + " from " + resourceCenter.getDefaultBaseURI());

			EMFMetaModelResource metaModelResource = metaModelRepository.getResource(ecoreMetaModelURI);

			if (metaModelResource != null) {
				ecoreMetaModelResource = metaModelResource;
				System.out.println("Found resource " + ecoreMetaModelURI);
			}
			else {
				System.out.println("Not found: " + ecoreMetaModelURI);
				for (FlexoResource<?> r : resourceCenter.getAllResources()) {
					System.out.println(" > " + r.getURI());
				}
			}

		}

		System.out.println("ecoreMetaModelResource=" + ecoreMetaModelResource);
		assertNotNull(ecoreMetaModelResource);

		ecoreMetaModel = ecoreMetaModelResource.getMetaModelData();
		assertNotNull(ecoreMetaModel);
		assertEquals(ecoreMetaModelURI, ecoreMetaModel.getURI());

		// Check that the classes declared in example.ecore are all available
		for (String className : new String[] { "NamedElement", "Market", "Actor", "Article" }) {
			assertNotNull("No class found for " + className, ecoreMetaModel.getClass(ecoreMetaModelURI + "/" + className));
		}
	}

	@Test
	@TestOrder(3)
	@Category(UITest.class)
	public void TestCreateOntologyBrowser() {

		assertNotNull(ecoreMetaModel);

		long previousDate, currentDate;
		int latency_time = 0;

		long startTime = System.currentTimeMillis();

		OntologyBrowserModel<EMFTechnologyAdapter> obm = new EMFMetaModelBrowserModel(ecoreMetaModel);

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
		System.out.println(" setShowIndividuals took: " + (currentDate - previousDate - latency_time));
		previousDate = currentDate;

		obm.setShowClasses(false);
		currentDate = System.currentTimeMillis();
		System.out.println(" setShowClasses took: " + (currentDate - previousDate - latency_time));
		previousDate = currentDate;

		obm.setShowDataProperties(false);
		currentDate = System.currentTimeMillis();
		System.out.println(" setShowDataProperties took: " + (currentDate - previousDate - latency_time));
		previousDate = currentDate;

		obm.setShowObjectProperties(false);
		currentDate = System.currentTimeMillis();
		System.out.println(" setShowObjectProperties took: " + (currentDate - previousDate - latency_time));
		previousDate = currentDate;

		obm.setShowAnnotationProperties(false);
		currentDate = System.currentTimeMillis();
		System.out.println(" setShowAnnotationProperties took: " + (currentDate - previousDate - latency_time));
		previousDate = currentDate;

		obm.recomputeStructure();
		currentDate = System.currentTimeMillis();
		System.out.println(" recomputeStructure took: " + (currentDate - previousDate - latency_time));
		previousDate = currentDate;

		obm.setShowClasses(true);
		currentDate = System.currentTimeMillis();
		System.out.println(" setShowClasses took: " + (currentDate - previousDate - latency_time));
		previousDate = currentDate;

		obm.setShowDataProperties(true);
		currentDate = System.currentTimeMillis();
		System.out.println(" setShowDataProperties took: " + (currentDate - previousDate - latency_time));
		previousDate = currentDate;

		obm.setShowObjectProperties(true);
		currentDate = System.currentTimeMillis();
		System.out.println(" setShowObjectProperties took: " + (currentDate - previousDate - latency_time));
		previousDate = currentDate;

		obm.setShowAnnotationProperties(true);
		currentDate = System.currentTimeMillis();
		System.out.println(" setShowAnnotationProperties took: " + (currentDate - previousDate - latency_time));
		previousDate = currentDate;

		obm.setShowIndividuals(true);
		currentDate = System.currentTimeMillis();
		System.out.println(" setShowIndividuals took: " + (currentDate - previousDate - latency_time));
		previousDate = currentDate;

		obm.recomputeStructure();
		currentDate = System.currentTimeMillis();
		System.out.println(" recomputeStructure took: " + (currentDate - previousDate - latency_time));
		previousDate = currentDate;

		obm.setShowIndividuals(false);
		currentDate = System.currentTimeMillis();
		System.out.println(" setShowIndividuals took: " + (currentDate - previousDate - latency_time));
		previousDate = currentDate;

		System.out.println(" setShowClasses took: " + (currentDate - previousDate - latency_time));
		previousDate = currentDate;

		obm.setShowDataProperties(false);
		currentDate = System.currentTimeMillis();
		System.out.println(" setShowDataProperties took: " + (currentDate - previousDate - latency_time));
		previousDate = currentDate;

		obm.setShowObjectProperties(false);
		currentDate = System.currentTimeMillis();
		System.out.println(" setShowObjectProperties took: " + (currentDate - previousDate - latency_time));
		previousDate = currentDate;

		obm.setShowAnnotationProperties(false);
		currentDate = System.currentTimeMillis();
		System.out.println(" setShowAnnotationProperties took: " + (currentDate - previousDate - latency_time));
		previousDate = currentDate;

		obm.recomputeStructure();
		currentDate = System.currentTimeMillis();
		System.out.println(" recomputeStructure took: " + (currentDate - previousDate - latency_time));
		previousDate = currentDate;
	}

	@Test
	@TestOrder(4)
	@Category(UITest.class)
	public void TestCreateEMFMetaModelView() throws InterruptedException {

		assertNotNull(ecoreMetaModel);

		long previousDate, currentDate;
		int latency_time = 1000;

		logger.info("TestCreateEMFMetaModelView");

		previousDate = System.currentTimeMillis();

		EMFMetaModelView modelView = new EMFMetaModelView(ecoreMetaModel, null, null);
		currentDate = System.currentTimeMillis();
		System.out.println(" initial creation of view took : " + (currentDate - previousDate - latency_time));
		previousDate = currentDate;

		gcDelegate.addTab("ecoreMetaModelView", modelView.getFIBController());

		previousDate = System.currentTimeMillis();

		int i = 2;

		while (i > 0) {
			i--;
			modelView.setShowIndividuals(false);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowIndividuals (FALSE)  took: " + (currentDate - previousDate - latency_time));
			previousDate = currentDate;

			modelView.setShowClasses(false);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowClasses (FALSE)  took: " + (currentDate - previousDate - latency_time));
			previousDate = currentDate;
			Thread.sleep(latency_time);

			modelView.setShowDataProperties(false);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowDataProperties (FALSE)  took: " + (currentDate - previousDate - latency_time));
			previousDate = currentDate;
			Thread.sleep(latency_time);

			modelView.setShowObjectProperties(false);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowObjectProperties (FALSE)  took: " + (currentDate - previousDate - latency_time));
			previousDate = currentDate;
			Thread.sleep(latency_time);

			modelView.setShowAnnotationProperties(false);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowAnnotationProperties (FALSE)  took: " + (currentDate - previousDate - latency_time));
			previousDate = currentDate;
			Thread.sleep(latency_time);

			modelView.update();
			currentDate = System.currentTimeMillis();
			System.out.println(" update   took: " + (currentDate - previousDate - latency_time));
			previousDate = currentDate;
			Thread.sleep(latency_time);

			// showing only individuals

			modelView.setShowIndividuals(true);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowIndividuals (TRUE) took: " + (currentDate - previousDate - latency_time));
			previousDate = currentDate;
			Thread.sleep(latency_time);

			modelView.setShowIndividuals(false);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowIndividuals (FALSE) took: " + (currentDate - previousDate - latency_time));
			previousDate = currentDate;
			Thread.sleep(latency_time);

			modelView.setShowClasses(true);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowClasses (TRUE)  took: " + (currentDate - previousDate - latency_time));
			previousDate = currentDate;
			Thread.sleep(latency_time);

			modelView.setShowDataProperties(true);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowDataProperties (TRUE) took: " + (currentDate - previousDate - latency_time));
			previousDate = currentDate;
			Thread.sleep(latency_time);

			modelView.setShowObjectProperties(true);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowObjectProperties (TRUE)  took: " + (currentDate - previousDate - latency_time));
			previousDate = currentDate;

			modelView.setShowAnnotationProperties(true);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowAnnotationProperties (TRUE) took: " + (currentDate - previousDate - latency_time));
			previousDate = currentDate;
			Thread.sleep(latency_time);

			modelView.setShowIndividuals(true);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowIndividuals (TRUE) took: " + (currentDate - previousDate - latency_time));
			previousDate = currentDate;
			Thread.sleep(latency_time);

			modelView.update();
			currentDate = System.currentTimeMillis();
			System.out.println(" update  took: " + (currentDate - previousDate - latency_time));
			previousDate = currentDate;
			Thread.sleep(latency_time);

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
		gcDelegate = new SwingGraphicalContextDelegate(TestEcoreOntologyBrowerModel.class.getSimpleName());
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
