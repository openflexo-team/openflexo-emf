package org.openflexo.technologyadapter.emf.model.capella;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyIndividual;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.test.OpenflexoTestCase;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.EMFTechnologyContextManager;
import org.openflexo.technologyadapter.emf.metamodel.EMFClassClass;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelResource;
import org.openflexo.technologyadapter.emf.rm.EMFModelResource;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;
import org.eclipse.emf.ecore.resource.Resource;
@RunWith(OrderedRunner.class)
public class TestLoadCapellaModel extends OpenflexoTestCase {

    protected static final Logger logger = Logger.getLogger(TestLoadCapellaModel.class.getPackage().getName());

    private static EMFMetaModelResource capellaMMRes;
    private static EMFModelResource capellaModelRes;

    private static EMFMetaModel metaModel;
    private static EMFModel model;

    @Test
    @TestOrder(1)
    public void testInitializeServiceManager() throws Exception {
        log("testInitializeServiceManager()");
        instanciateTestServiceManager(EMFTechnologyAdapter.class);
    }
    
    @Test
    @TestOrder(2)
    public void listRegisteredResources() {
    	serviceManager.getResourceCenterService().getResourceCenters().forEach(rc -> {
    	    System.out.println("Resource center: " + rc.getDefaultBaseURI());
    	});
    
        System.out.println(">>> Ressources enregistrées :");
        serviceManager.getResourceManager().getRegisteredResources().forEach(r ->
            System.out.println(" - " + r.getURI() + " : " + r));
    }

    
    @Test
    @TestOrder(3)
    public void lookupCapellaMetaModel() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {
        log("lookupCapellaMetaModel()");

        capellaMMRes = (EMFMetaModelResource) serviceManager.getResourceManager()
            .getResource("http://www.polarsys.org/capella/core/modeller/7.0.0", EMFMetaModel.class);

        assertNotNull("Le métamodèle Capella doit être trouvé", capellaMMRes);
        assertFalse("Le métamodèle ne doit pas être encore chargé", capellaMMRes.isLoaded());


        metaModel = capellaMMRes.getResourceData();


        System.out.println("metaModel = " + metaModel);
        System.out.println("ePackage = " + (metaModel != null ? metaModel.getRootPackage() : "null"));



        assertTrue("Le métamodèle doit être chargé", capellaMMRes.isLoaded());
        assertNotNull("Le métamodèle Capella doit être non nul", metaModel);
        
        EMFTechnologyContextManager ctxManager = (EMFTechnologyContextManager)
        	    serviceManager.getTechnologyAdapterService().getTechnologyAdapter(EMFTechnologyAdapter.class).getTechnologyContextManager();
        
        ctxManager.getAllMetaModelURIs().forEach(r -> System.out.println(r + "\n" ));
    }

    @Test
    @TestOrder(4)
    public void loadCapellaModelResource() {
        log("loadCapellaModelResource()");
        
        System.out.println("=== All loaded resources ===");
        serviceManager.getResourceManager().getRegisteredResources().forEach(res -> {
            System.out.println("Resource: " + res.getURI() );
        });
        System.out.println("============================");

        capellaModelRes = (EMFModelResource) serviceManager.getResourceManager()
            .getResource("http://openflexo.org/emf-test/TestResourceCenter/EMF/Model/capella/Exercicedrone.capella", EMFModel.class);
        
        
        System.out.println("capellaModelRes = " + capellaModelRes + "\n");
        System.out.println("capellaModelRes.getMetaModelResource() = " + capellaModelRes.getMetaModelResource()+ "\n");

        
        System.out.println("Metamodel resource URI = " + capellaMMRes.getURI());
        System.out.println("Loaded model metamodel URI = " + capellaModelRes.getMetaModelResource().getURI());

        
        assertNotNull("La ressource modèle Capella doit être trouvée", capellaModelRes);
        assertSame("Le métamodèle doit correspondre", capellaMMRes, capellaModelRes.getMetaModelResource());
    }

    
    @Test
    @TestOrder(5)
    public void loadCapellaModel() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {
        log("loadCapellaModel()");
        
        assertNotNull("La ressource du modèle Capella ne doit pas être null", capellaModelRes);
        assertFalse("Le modèle ne doit pas être encore chargé", capellaModelRes.isLoaded());
        assertNotNull("La ressource du métamodèle Capella ne doit pas être null", capellaMMRes);
        assertNotNull("Le métamodèle Capella doit avoir un ResourceCenter", capellaMMRes.getResourceCenter());
        assertTrue("La ressource du métamodèle doit être chargée", capellaMMRes.isLoaded());
        
        model = capellaModelRes.getResourceData();
        
        assertNotNull("Le modèle Capella doit être chargé", model);
        assertNotNull("La ressource EMF du modèle ne doit pas être null", model.getEMFResource());
        assertFalse("La ressource EMF doit contenir au moins un élément", model.getEMFResource().getContents().isEmpty());

        assertTrue("La ressource du modèle est maintenant chargée", capellaModelRes.isLoaded());
        Object root = model.getEMFResource().getContents().get(0);
        System.out.println("Modèle Capella chargé avec racine : " + root);
    }
    
    @Test
    @TestOrder(6)
    public void performSomeTests() {
    	log("performSomeTests");
    	
    	IFlexoOntologyClass<EMFTechnologyAdapter> systemEngineeringClass = metaModel
                .getClass("http://www.polarsys.org/capella/core/modeller/7.0.0/SystemEngineering");
    	
    	System.out.println("AAAAAAAA");
    	assertNotNull("La classe SystemEngineering doit exister", systemEngineeringClass);
        
        List<? extends IFlexoOntologyIndividual<EMFTechnologyAdapter>> systemEngineerings =
                model.getIndividuals(systemEngineeringClass);

        assertNotNull("Les individus de SystemEngineering ne doivent pas être null", systemEngineerings);
        assertFalse("Il doit y avoir au moins une instance de SystemEngineering", systemEngineerings.isEmpty());
    }
}
