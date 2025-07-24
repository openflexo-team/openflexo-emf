package org.openflexo.technologyadapter.emf.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyFeatureAssociation;
import org.openflexo.foundation.test.OpenflexoTestCase;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelResource;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

@RunWith(OrderedRunner.class)
public class ECoreMetaDataCacheTest extends OpenflexoTestCase{
	private static EMFMetaModel metaModel;
	private static EMFMetaModelResource metaModelRes;
	
	@Test
    @TestOrder(1)
    public void testInitializeServiceManager() throws Exception {
        log("testInitializeServiceManager()");
        instanciateTestServiceManager(EMFTechnologyAdapter.class);
    }
	
	@Test
    @TestOrder(2)
    public void listRegisteredResources() {
		log("listRegisteredResources()");
		
    	serviceManager.getResourceCenterService().getResourceCenters().forEach(rc -> {
    	    System.out.println("Resource center: " + rc.getDefaultBaseURI());
    	});
    
        System.out.println(">>> Ressources enregistrées :");
        serviceManager.getResourceManager().getRegisteredResources().forEach(r ->
            System.out.println(" - " + r.getURI() + " : " + r));
    }
	
	@Test
	@TestOrder(3)
    public void testLoadTestMetaModel() throws Exception {
        log("testLoadTestMetaModel()");
        
        metaModelRes = (EMFMetaModelResource) serviceManager.getResourceManager()
                .getResource("http://example.org/b", EMFMetaModel.class);

        assertNotNull("Le métamodèle doit être trouvé", metaModelRes);

        metaModel = metaModelRes.getResourceData();

        assertNotNull("Le métamodèle doit être chargé", metaModel);
    }
	
	@Test
	@TestOrder(4)
	public void testLoadClasses() {
		for (IFlexoOntologyClass<EMFTechnologyAdapter> emfClass : metaModel.getClasses()) {
	        System.out.println("Classe: " + emfClass.getName() + " (" + emfClass.getURI() + ")");

	        try {
	            List<? extends IFlexoOntologyFeatureAssociation<EMFTechnologyAdapter>> fas =
	                    emfClass.getStructuralFeatureAssociations();

	            System.out.println("Nombre d'associations structurelles: " + fas.size());
	            fas.forEach(fa -> System.out.println(" - " + fa));
	        } catch (StackOverflowError e) {
	            System.err.println("StackOverflowError détecté pour la classe: " + emfClass.getName());
	            assertTrue("La méthode crée une boucle infinie sur " + emfClass.getName(), false);
	        }
	    }

	}

}
