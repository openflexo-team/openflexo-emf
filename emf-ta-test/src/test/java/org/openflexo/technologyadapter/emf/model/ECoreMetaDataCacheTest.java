package org.openflexo.technologyadapter.emf.model;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.eclipse.emf.ecore.EClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.test.OpenflexoTestCase;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelResource;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

@RunWith(OrderedRunner.class)
public class ECoreMetaDataCacheTest extends OpenflexoTestCase{
	private static EMFMetaModel metaModel;
	
	@Test
    @TestOrder(1)
    public void testInitializeServiceManager() throws Exception {
        log("testInitializeServiceManager()");
        instanciateTestServiceManager(EMFTechnologyAdapter.class);
    }
	
	@Test
	@TestOrder(2)
    public void testMetadataCacheFailsWithMultipleEcoreFiles() {
		File baseDir = new File("/home/azim/Documents/Stage2/openflexo-emf/emf-ta-test-rc/src/main/resources/TestResourceCenter/EMF/MetaModel/MultipleECore/");
		File aEcore = new File(baseDir, "A.ecore");
	    File bEcore = new File(baseDir, "B.ecore");
	    
	    System.out.println("Absolute path: " + baseDir.getAbsolutePath());
	    System.out.println("Absolute path: " + aEcore.getAbsolutePath());
	    System.out.println("Absolute path: " + bEcore.getAbsolutePath());
	    
	    assertTrue("A.ecore must exist", aEcore.exists());
	    assertTrue("B.ecore must exist", bEcore.exists());
	    
	    EMFMetaModelResource mMRes = (EMFMetaModelResource) serviceManager.getResourceManager()
        .getResource("");
	    
	}

}
