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

import java.io.FileNotFoundException;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.test.OpenflexoTestCase;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.metamodel.EMFClassClass;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.rm.ECoreMetaModelResource;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Test SOAML Meta-Model
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class TestBasicECoreMetaModel extends OpenflexoTestCase {
	protected static final Logger logger = Logger.getLogger(TestBasicECoreMetaModel.class.getPackage().getName());

	private static ECoreMetaModelResource soamlMetaModelResource;
	private static EMFMetaModel metaModel;

	// private static EMFMetaModel metaModel;
	// private static EMFModel city1Model;

	@Test
	@TestOrder(1)
	public void testInitializeServiceManager() throws Exception {
		log("test0InstantiateResourceCenter()");

		instanciateTestServiceManager(EMFTechnologyAdapter.class);

		/*for (FlexoResource<?> r : serviceManager.getResourceManager().getRegisteredResources()) {
			System.out.println(" > " + r.getURI());
		}*/

	}

	@Test
	@TestOrder(2)
	public void lookupSOAMLMetaModel() {

		soamlMetaModelResource = (ECoreMetaModelResource) serviceManager.getResourceManager()
				.getResource("http://www.example.org/metamodelProject/Metamodel/SOAMLmetaModel", EMFMetaModel.class);

		assertNotNull(soamlMetaModelResource);
		assertEquals("SoamlMetaModel", soamlMetaModelResource.getName());
		assertEquals("http://www.example.org/metamodelProject/Metamodel/SOAMLmetaModel", soamlMetaModelResource.getURI());

	}

	@Test
	@TestOrder(3)
	public void loadSOAMLMetaModel() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {

		soamlMetaModelResource.loadResourceData();

		assertNotNull(metaModel = soamlMetaModelResource.getLoadedResourceData());

		for (IFlexoOntologyClass<EMFTechnologyAdapter> iFlexoOntologyClass : metaModel.getClasses()) {
			System.out.println(" > " + iFlexoOntologyClass + " " + iFlexoOntologyClass.getOntology());
			if (iFlexoOntologyClass instanceof EMFClassClass) {
				EClass eClass = ((EMFClassClass) iFlexoOntologyClass).getObject();
				System.out.println(eClass.eContainer());
			}
		}

		// metaModel.getAccessibleClasses()A

	}

	/*@Test
	@TestOrder(2)
	public void lookupSOAMLMetaModel() {
	
		// metaModelResource = (EMFMetaModelResource) serviceManager.getResourceManager()
		// .getResource("http://openflexo.org/emf-test/TestResourceCenter/EMF/Ecore/SOAMLmeta.ecore", EMFMetaModel.class);
	
		EMFMetaModelResource proutRes = (EMFMetaModelResource) serviceManager.getResourceManager()
				.getResource("http://openflexo.org/emf-test/TestResourceCenter/EMF/Ecore/SOAMLmeta.ecore", EMFMetaModel.class);
	
		assertNotNull(proutRes);
	
		File f = (File) proutRes.getIODelegate().getSerializationArtefact();
		log("file: " + f + " exists=" + f.exists());
	
		// TODO Auto-generated method stub
		// Create a resource set.
		ResourceSet resourceSet = new ResourceSetImpl();
	
		// Register the default resource factory -- only needed for stand-alone!
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
	
		// Register the package -- only needed for stand-alone!
		EcorePackage ecorePackage = EcorePackage.eINSTANCE;
	
		// Get the URI of the model file.
		// URI fileURI = URI.createPlatformPluginURI("platform:/plugin/org.eclipse.mymetamodel/model/MyMetaModel.ecore", false);
		URI fileURI = URI.createFileURI(f.getAbsolutePath());
	
		// Demand load the resource for this file.
		Resource resource = resourceSet.getResource(fileURI, true);
	
		// Print the contents of the resource to System.out.
		try {
			resource.save(System.out, Collections.EMPTY_MAP);
		} catch (IOException e) {
		}
	
		EMFMetaModelConverter converter = new EMFMetaModelConverter(
				serviceManager.getTechnologyAdapterService().getTechnologyAdapter(EMFTechnologyAdapter.class));
		EMFMetaModel mm = converter.convertMetaModel(resource);
	
		System.out.println("mm=" + mm);
		System.out.println("all_classes=" + mm.getAccessibleClasses());
		System.out.println("classes=" + mm.getClasses());
	
	}*/

}
