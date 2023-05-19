/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Flexodiagram, a component of the software infrastructure 
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyIndividual;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.test.OpenflexoProjectAtRunTimeTestCase;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.rm.EMFModelResource;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Test the instantiation of a VirtualModel whose instances have {@link FMLControlledDiagramVirtualModelNature}
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class TestLoadSimpleBPMNProcess extends OpenflexoProjectAtRunTimeTestCase {

	private static EMFModelResource basicExampleResource;
	private static EMFModel model;
	private static EMFMetaModel metaModel;

	@Test
	@TestOrder(1)
	public void testLoadEMFModel() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {
		instanciateTestServiceManager(EMFTechnologyAdapter.class);

		EMFTechnologyAdapter emfTA = serviceManager.getTechnologyAdapterService().getTechnologyAdapter(EMFTechnologyAdapter.class);
		serviceManager.activateTechnologyAdapter(emfTA, true);

		FlexoResourceCenter<?> resourceCenter = serviceManager.getResourceCenterService()
				.getFlexoResourceCenter("http://openflexo.org/owl-test");

		System.out.println("emfTA=" + emfTA);
		System.out.println("resourceCenter=" + resourceCenter);

		for (FlexoResource<?> flexoResource : serviceManager.getResourceManager().getRegisteredResources()) {
			System.out.println("> " + flexoResource + " loaded: " + flexoResource.isLoaded() + " uri=" + flexoResource.getURI());
		}

		basicExampleResource = (EMFModelResource) serviceManager.getResourceManager()
				.getResource("http://openflexo.org/emf-test/TestResourceCenter/EMF/Model/bpmn/BasicExample.bpmn");
		assertNotNull(basicExampleResource);

		model = basicExampleResource.getModel();
		assertNotNull(model);

		metaModel = model.getMetaModel();
		assertNotNull(metaModel);
		assertEquals("http://www.omg.org/spec/BPMN/20100524/MODEL-XMI", metaModel.getURI());

		for (IFlexoOntologyClass<EMFTechnologyAdapter> c : metaModel.getAccessibleClasses()) {
			System.out.println(" >>>> " + c.getName());
		}
		assertEquals(195, metaModel.getAccessibleClasses().size());

		for (IFlexoOntologyIndividual<EMFTechnologyAdapter> ind : model.getIndividuals()) {
			System.out.println(" > " + ind + " of " + ind.getTypes());
		}
		assertEquals(34, model.getIndividuals().size());

	}
}
