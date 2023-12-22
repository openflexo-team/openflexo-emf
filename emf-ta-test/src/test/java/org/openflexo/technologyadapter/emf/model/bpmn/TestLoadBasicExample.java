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

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyIndividual;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.test.OpenflexoTestCase;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.metamodel.EMFClassClass;
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
	public void lookupBPMNMetaModel() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {

		bpmnMMRes = (EMFMetaModelResource) serviceManager.getResourceManager()
				.getResource("http://www.omg.org/spec/BPMN/20100524/MODEL-XMI", EMFMetaModel.class);

		assertNotNull(bpmnMMRes);

		assertFalse(bpmnMMRes.isLoaded());
		metaModel = bpmnMMRes.getResourceData();
		assertTrue(bpmnMMRes.isLoaded());
		assertNotNull(metaModel);

		IFlexoOntologyClass<EMFTechnologyAdapter> definitionsClass = metaModel
				.getClass("http://www.omg.org/spec/BPMN/20100524/MODEL-XMI/Definitions");
		assertNotNull(definitionsClass);

		IFlexoOntologyClass<EMFTechnologyAdapter> diagramClass = metaModel
				.getClass("http://www.omg.org/spec/BPMN/20100524/DI-XMI/BPMNDiagram");
		assertNotNull(diagramClass);

	}

	@Test
	@TestOrder(3)
	public void loadBasicModelResource() {

		basicModelRes = (EMFModelResource) serviceManager.getResourceManager()
				.getResource("http://openflexo.org/emf-test/TestResourceCenter/EMF/Model/bpmn/BasicExample.bpmn", EMFModel.class);
		/*for (FlexoResource<?> resource : serviceManager.getResourceManager().getRegisteredResources()) {
			System.out.println(" > " + resource.getURI() + " : " + resource);
		}*/

		assertNotNull(basicModelRes);
		assertSame(bpmnMMRes, basicModelRes.getMetaModelResource());

	}

	@Test
	@TestOrder(4)
	public void loadBasicModel() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {

		assertFalse(basicModelRes.isLoaded());

		basicModel = basicModelRes.getResourceData();
		assertNotNull(basicModel);

		assertTrue(basicModelRes.isLoaded());
		assertTrue(bpmnMMRes.isLoaded());

		/*for (IFlexoOntologyIndividual<EMFTechnologyAdapter> iFlexoOntologyIndividual : basicModel.getIndividuals()) {
			System.out.println(" > Individual " + iFlexoOntologyIndividual + " of " + iFlexoOntologyIndividual.getTypes());
		}*/
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
		assertNotNull(definitions);
		/*for (IFlexoOntologyPropertyValue<EMFTechnologyAdapter> propertyValue : definitions.getPropertyValues()) {
			System.out.println("propertyValue: " + propertyValue.getProperty().getName() + " : " + propertyValue.getValues() + " of "
					+ propertyValue.getValues().getClass());
			for (Object value : propertyValue.getValues()) {
				System.out.println(" > " + value + " of " + value.getClass());
			}
		}*/

		List<?> rootElements = definitions.getValues("rootElements");
		assertEquals(1, rootElements.size());

		EMFObjectIndividual process = (EMFObjectIndividual) rootElements.get(0);
		System.out.println("process: " + process);
		assertNotNull(process);

		System.out.println("ID: " + EcoreUtil.getID(process.getObject()));
		assertEquals("Process_172zuw6", EcoreUtil.getID(process.getObject()));

		List<?> diagrams = definitions.getValues("diagrams");
		assertEquals(1, diagrams.size());
		EMFObjectIndividual diagram = (EMFObjectIndividual) diagrams.get(0);
		System.out.println("diagram: " + diagram);
		System.out.println("class: " + diagram.getTypes());

		EMFClassClass bpmnDiagramClass = (EMFClassClass) diagram.getTypes().get(0);
		System.out.println("URI: " + bpmnDiagramClass.getURI());

		IFlexoOntologyClass<EMFTechnologyAdapter> diagramClass = metaModel
				.getClass("http://www.omg.org/spec/BPMN/20100524/DI-XMI/BPMNDiagram");
		assertNotNull(diagramClass);

		assertSame(diagram.getTypes().get(0), diagramClass);

		EMFObjectIndividual plane = (EMFObjectIndividual) diagram.getValues("plane").get(0);
		assertNotNull(plane);
		System.out.println("plane: " + plane);

		EMFObjectIndividual bpmnElement = (EMFObjectIndividual) plane.getValues("bpmnElement").get(0);
		assertNotNull(bpmnElement);
		System.out.println("bpmnElement: " + bpmnElement);
		assertSame(bpmnElement, process);

		/*EObject ePlane = plane.getObject();
		System.out.println("ePlane=" + ePlane);
		System.out.println("isProxy: " + ePlane.eIsProxy());
		EStructuralFeature bpmnElementF = ePlane.eClass().getEStructuralFeature("bpmnElement");
		System.out.println("bpmnElementF=" + bpmnElementF);
		Object el = ePlane.eGet(bpmnElementF);
		System.out.println("el=" + el);
		EObject el2 = (EObject) ePlane.eGet(bpmnElementF);
		System.out.println("el2=" + el2);
		System.out.println("isProxy: " + el2.eIsProxy());
		EObject resolve = EcoreUtil.resolve(el2, basicModel.getEMFResource());
		System.out.println("resolve=" + resolve);
		System.out.println("isProxy=" + resolve.eIsProxy());
		
		URI proxyURI = ((InternalEObject) el2).eProxyURI();
		System.out.println("proxyURI=" + proxyURI);
		EObject eObj = basicModel.getEMFResource().getEObject(proxyURI.fragment());
		System.out.println("eObj=" + eObj);
		System.out.println("isProxy=" + eObj.eIsProxy());*/

	}

}
