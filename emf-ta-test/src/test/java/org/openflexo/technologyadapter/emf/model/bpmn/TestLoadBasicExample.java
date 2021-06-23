/**
 * 
 * Copyright (c) 2015, Openflexo
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

package org.openflexo.technologyadapter.emf.model.bpmn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyIndividual;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.test.OpenflexoTestCase;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividual;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelResource;
import org.openflexo.technologyadapter.emf.rm.EMFModelResource;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Test BPMN basic example
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class TestLoadBasicExample extends OpenflexoTestCase {
	protected static final Logger logger = Logger.getLogger(TestLoadBasicExample.class.getPackage().getName());

	private static EMFMetaModelResource bpmnMMRes;
	private static EMFModelResource basicModelRes;

	private static EMFMetaModel metaModel;
	private static EMFModel basicModel;

	@Test
	@TestOrder(1)
	public void testInitializeServiceManager() throws Exception {
		log("test0InstantiateResourceCenter()");

		instanciateTestServiceManager(EMFTechnologyAdapter.class);

	}

	@Test
	@TestOrder(2)
	public void lookupBPMNMetaModel() {

		bpmnMMRes = (EMFMetaModelResource) serviceManager.getResourceManager()
				.getResource("http://www.omg.org/spec/BPMN/20100524/MODEL-XMI", EMFMetaModel.class);

		assertNotNull(bpmnMMRes);

	}

	@Test
	@TestOrder(3)
	public void lookupCity1Model() {

		basicModelRes = (EMFModelResource) serviceManager.getResourceManager()
				.getResource("http://openflexo.org/emf-test/TestResourceCenter/EMF/Model/BPMN/BasicExample.bpmn", EMFModel.class);

		assertNotNull(basicModelRes);
		assertSame(bpmnMMRes, basicModelRes.getMetaModelResource());

	}

	@Test
	@TestOrder(4)
	public void loadBasicModel() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {

		assertFalse(basicModelRes.isLoaded());
		assertFalse(bpmnMMRes.isLoaded());

		basicModel = basicModelRes.getResourceData();
		assertNotNull(basicModel);

		assertTrue(basicModelRes.isLoaded());
		assertTrue(bpmnMMRes.isLoaded());

		metaModel = bpmnMMRes.getLoadedResourceData();
		assertNotNull(metaModel);

		for (IFlexoOntologyIndividual<EMFTechnologyAdapter> iFlexoOntologyIndividual : basicModel.getIndividuals()) {
			System.out.println(" > Individual " + iFlexoOntologyIndividual + " of " + iFlexoOntologyIndividual.getTypes());
		}
	}

	@Test
	@TestOrder(5)
	public void performSomeTests() {

		/*for (IFlexoOntologyClass<EMFTechnologyAdapter> c : metaModel.getClasses()) {
			System.out.println(" > Class : " + c.getName() + " URI=" + c.getURI());
		}*/

		IFlexoOntologyClass<EMFTechnologyAdapter> definitionsClass = metaModel
				.getClass("http://www.omg.org/spec/BPMN/20100524/MODEL-XMI/Definitions");
		assertNotNull(definitionsClass);

		List<? extends IFlexoOntologyIndividual<EMFTechnologyAdapter>> definitionsList = basicModel.getIndividuals(definitionsClass);
		assertNotNull(definitionsList);
		assertEquals(1, definitionsList.size());
		EMFObjectIndividual definitions = (EMFObjectIndividual) definitionsList.get(0);

		System.out.println("definitions = " + definitions);
		/*for (IFlexoOntologyPropertyValue<EMFTechnologyAdapter> propertyValue : definitions.getPropertyValues()) {
			System.out.println("propertyValue: " + propertyValue.getProperty().getName() + " : " + propertyValue.getValues() + " of "
					+ propertyValue.getValues().getClass());
			for (Object value : propertyValue.getValues()) {
				System.out.println(" > " + value + " of " + value.getClass());
			}
		}*/

		List<?> values = definitions.getValues("rootElements");
		assertEquals(1, values.size());

		EMFObjectIndividual process = (EMFObjectIndividual) values.get(0);
		System.out.println("process: " + process);
		assertNotNull(process);

	}

	/*
	@Test
	@TestOrder(6)
	public void performSomeOtherTests() {
	
		for (IFlexoOntologyClass<EMFTechnologyAdapter> c : metaModel.getAccessibleClasses()) {
			System.out.println("> class " + c.getURI());
		}
	
		EMFClassClass cityClass = (EMFClassClass) metaModel.getClass("http://www.thalesgroup.com/openflexo/emf/model/city1/City");
		assertNotNull(cityClass);
	
		// assertSame(cityClass,
		// metaModel.getDeclaredClass("http://www.thalesgroup.com/openflexo/emf/model/city1/City"));
	
		Resource resource = city1Model.getEMFResource();
		List<EObject> selectedEMFIndividuals = new ArrayList<>();
		TreeIterator<EObject> iterator = resource.getAllContents();
		while (iterator.hasNext()) {
			EObject eObject = iterator.next();
			System.out.println("Found " + eObject);
			EMFClassClass eObjectType = city1Model.getMetaModel().getConverter().getClasses().get(eObject.eClass());
			if (eObjectType.equals(cityClass) || cityClass.isSuperClassOf(eObjectType)) {
				selectedEMFIndividuals.add(eObject);
			}
		}
	
		System.out.println("selectedEMFIndividuals=" + selectedEMFIndividuals);
	
		assertEquals(4, selectedEMFIndividuals.size());
	
	}*/

}
