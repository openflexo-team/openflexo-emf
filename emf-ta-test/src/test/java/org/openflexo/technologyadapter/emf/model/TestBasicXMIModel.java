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

package org.openflexo.technologyadapter.emf.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.io.FileNotFoundException;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.test.OpenflexoTestCase;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.metamodel.EMFClassClass;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.rm.ECoreMetaModelResource;
import org.openflexo.technologyadapter.emf.rm.EMFModelResource;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Test SOAML Meta-Model
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class TestBasicXMIModel extends OpenflexoTestCase {
	protected static final Logger logger = Logger.getLogger(TestBasicXMIModel.class.getPackage().getName());

	private static ECoreMetaModelResource soamlMetaModelResource;

	private static EMFModelResource modelResource;
	private static EMFMetaModel metaModel;
	private static EMFModel model;

	private static EMFClassClass serviceArchitectureClass;
	private static EMFClassClass collaborationUseClass;
	private static EMFClassClass participantRoleClass;
	private static EMFClassClass bindingClass;

	@Test
	@TestOrder(1)
	public void testInitializeServiceManager() throws Exception {
		log("test0InstantiateResourceCenter()");

		instanciateTestServiceManager(EMFTechnologyAdapter.class);

		for (FlexoResource<?> r : serviceManager.getResourceManager().getRegisteredResources()) {
			System.out.println(" > " + r.getURI());
		}

	}

	@Test
	@TestOrder(2)
	public void lookupSOAMLMetaModel() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {

		soamlMetaModelResource = (ECoreMetaModelResource) serviceManager.getResourceManager()
				.getResource("http://www.example.org/metamodelProject/Metamodel/SOAMLmetaModel", EMFMetaModel.class);

		assertNotNull(soamlMetaModelResource);
		assertEquals("SoamlMetaModel", soamlMetaModelResource.getName());
		assertEquals("http://www.example.org/metamodelProject/Metamodel/SOAMLmetaModel", soamlMetaModelResource.getURI());

		metaModel = soamlMetaModelResource.getResourceData();
		assertNotNull(serviceArchitectureClass = (EMFClassClass) metaModel.getDeclaredClass("Service_Architecture"));
		assertNotNull(collaborationUseClass = (EMFClassClass) metaModel.getDeclaredClass("CollaborationUse"));
		assertNotNull(participantRoleClass = (EMFClassClass) metaModel.getDeclaredClass("ParticipantRole"));
		assertNotNull(bindingClass = (EMFClassClass) metaModel.getDeclaredClass("BindingLink"));

	}

	@Test
	@TestOrder(3)
	public void lookupXMIModel() {

		modelResource = (EMFModelResource) serviceManager.getResourceManager().getResource(
				"http://openflexo.org/emf-test/TestResourceCenter/EMF/XMI/Service_Architecture_BookingHoliday.xmi", EMFModel.class);

		assertNotNull(modelResource);
		assertSame(soamlMetaModelResource, modelResource.getMetaModelResource());

	}

	@Test
	@TestOrder(4)
	public void loadXMIModel() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {

		modelResource.loadResourceData();

		assertNotNull(model = modelResource.getLoadedResourceData());

		for (EMFObjectIndividual individual : model.getIndividuals()) {
			System.out.println(" > " + individual + " of " + individual.getTypes());
		}

		assertEquals(12, model.getIndividuals().size());
		assertEquals(1, model.getIndividuals(serviceArchitectureClass).size());
		assertEquals(5, model.getIndividuals(collaborationUseClass).size());
		assertEquals(5, model.getIndividuals(participantRoleClass).size());
		assertEquals(1, model.getIndividuals(bindingClass).size());

		EMFObjectIndividual serviceArchitecture = model.getIndividuals(serviceArchitectureClass).get(0);

	}

}
