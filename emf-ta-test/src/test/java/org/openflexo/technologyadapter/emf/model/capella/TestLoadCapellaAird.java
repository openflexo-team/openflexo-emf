package org.openflexo.technologyadapter.emf.model.capella;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.test.OpenflexoTestCase;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelResource;
import org.openflexo.technologyadapter.emf.rm.EMFModelResource;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

@RunWith(OrderedRunner.class)
public class TestLoadCapellaAird extends OpenflexoTestCase {

    private static EMFMetaModelResource capellaMMAirdRes;
    private static EMFModelResource capellaModelAirdRes;
    private static EMFMetaModel metaModelAird;
    private static EMFModel modelAird;

    @Test
    @TestOrder(1)
    public void testInitializeServiceManager() throws Exception {
        log("testInitializeServiceManager()");
        instanciateTestServiceManager(EMFTechnologyAdapter.class);
    }

    @Test
    @TestOrder(2)
    public void loadMetaModelAird() throws Exception {
        capellaMMAirdRes = (EMFMetaModelResource) serviceManager.getResourceManager()
            .getResource("http://www.eclipse.org/sirius/1.1.0", EMFMetaModel.class);
        assertNotNull(capellaMMAirdRes);
        assertFalse(capellaMMAirdRes.isLoaded());

        metaModelAird = capellaMMAirdRes.getResourceData();
        assertTrue(capellaMMAirdRes.isLoaded());
        assertNotNull(metaModelAird);
    }

    @Test
    @TestOrder(3)
    public void loadAirdModelResource() {
        capellaModelAirdRes = (EMFModelResource) serviceManager.getResourceManager()
            .getResource("http://openflexo.org/emf/Models/Capella7.0/Exercicedrone.aird", EMFModel.class);
        assertNotNull(capellaModelAirdRes);
        assertSame(capellaMMAirdRes, capellaModelAirdRes.getMetaModelResource());
    }

    @Test
    @TestOrder(4)
    public void loadAirdModel() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {
        assertFalse(capellaModelAirdRes.isLoaded());
        modelAird = capellaModelAirdRes.getResourceData();
        assertNotNull(modelAird);
        assertFalse(modelAird.getEMFResource().getContents().isEmpty());
        assertTrue(capellaModelAirdRes.isLoaded());

        Object rootAird = modelAird.getEMFResource().getContents().get(0);
        System.out.println("Modèle AIRD chargé avec racine : " + rootAird);
    }
}
