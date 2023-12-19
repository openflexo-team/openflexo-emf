/**
 * 
 * Copyright (c) 2014-2015, Openflexo
 * 
 * This file is part of Excelconnector, a component of the software infrastructure 
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

package org.openflexo.technologyadapter.emf.fml.editionaction;

import java.io.File;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.fml.annotations.FMLAttribute;
import org.openflexo.foundation.fml.editionaction.AbstractCreateResource;
import org.openflexo.foundation.fml.editionaction.EditionAction;
import org.openflexo.foundation.fml.rt.FMLExecutionException;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.technologyadapter.emf.EMFModelSlot;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelResource;
import org.openflexo.technologyadapter.emf.rm.EMFModelResource;
import org.openflexo.technologyadapter.emf.rm.EMFModelResourceFactory;
import org.openflexo.technologyadapter.emf.rm.JarBasedMetaModelResource;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.StringUtils;

/**
 * Abstract {@link EditionAction} allowing to create an {@link EMFModelResource}
 * 
 * 
 * @author sylvain
 *
 */
@ModelEntity(isAbstract = true)
public interface AbstractCreateEMFResource extends AbstractCreateResource<EMFModelSlot, EMFModel, EMFTechnologyAdapter> {

	@PropertyIdentifier(type = String.class)
	public static final String META_MODEL_URI_KEY = "metaModelURI";

	@Getter(value = META_MODEL_URI_KEY)
	@XMLAttribute
	@FMLAttribute(value = META_MODEL_URI_KEY, required = false, description = "<html>URI of the metamodel</html>")
	public String getMetaModelURI();

	@Setter(META_MODEL_URI_KEY)
	public void setMetaModelURI(String metaModelURI);

	public EMFMetaModel getMetaModel();

	public void setMetaModel(EMFMetaModel metaModel);

	public EMFMetaModelResource getMetaModelResource();

	public void setMetaModelResource(EMFMetaModelResource metaModelResource);

	public static abstract class AbstractCreateEMFResourceImpl
			extends AbstractCreateResourceImpl<EMFModelSlot, EMFModel, EMFTechnologyAdapter> implements AbstractCreateEMFResource {

		private static final Logger logger = Logger.getLogger(AbstractCreateEMFResource.class.getPackage().getName());

		private EMFMetaModelResource metaModelResource;
		private String metaModelURI;

		@Override
		public EMFMetaModelResource getMetaModelResource() {
			if (getAssignedFlexoProperty() instanceof EMFModelSlot) {
				return (EMFMetaModelResource) ((EMFModelSlot) getAssignedFlexoProperty()).getMetaModelResource();
			}
			if (metaModelResource == null && StringUtils.isNotEmpty(metaModelURI) && getModelSlotTechnologyAdapter() != null) {
				metaModelResource = (EMFMetaModelResource) getModelSlotTechnologyAdapter().getTechnologyContextManager()
						.getResourceWithURI(metaModelURI);
				logger.info("Looked-up " + metaModelResource);
			}
			return metaModelResource;
		}

		@Override
		public void setMetaModelResource(EMFMetaModelResource metaModelResource) {
			if (getAssignedFlexoProperty() instanceof EMFModelSlot) {
				((EMFModelSlot) getAssignedFlexoProperty()).setMetaModelResource(metaModelResource);
			}
			this.metaModelResource = metaModelResource;
		}

		@Override
		public String getMetaModelURI() {
			if (metaModelResource != null) {
				return metaModelResource.getURI();
			}
			return metaModelURI;
		}

		@Override
		public void setMetaModelURI(String diagramSpecificationURI) {
			this.metaModelURI = diagramSpecificationURI;
		}

		@Override
		public EMFMetaModel getMetaModel() {
			if (getMetaModelResource() != null) {
				return getMetaModelResource().getMetaModel();
			}
			return null;
		}

		@Override
		public void setMetaModel(EMFMetaModel diagramSpecification) {
			metaModelResource = diagramSpecification.getResource();
		}

		@Override
		public Type getAssignableType() {
			return EMFModel.class;
		}

		protected abstract EMFModelResource getInitialResource(RunTimeEvaluationContext evaluationContext);

		/**
		 * Main action
		 */
		@Override
		public EMFModel execute(RunTimeEvaluationContext evaluationContext) throws FMLExecutionException {

			if (getMetaModelResource() == null) {
				logger.warning("No metamodel resource, cannot proceed");
				throw new FMLExecutionException("No metamodel resource, cannot proceed");
			}

			FlexoResourceCenter<?> rc = getResourceCenter(evaluationContext);
			if (rc instanceof FlexoProject) {
				rc = ((FlexoProject) rc).getDelegateResourceCenter();
			}
			if (!(rc instanceof FileSystemBasedResourceCenter)) {
				logger.warning("Cannot create an EMFModel elsewhere than in a FileSystemBaseResourceCenter");
				throw new FMLExecutionException("Cannot create an EMFModel elsewhere than in a FileSystemBaseResourceCenter");
			}

			logger.info("Create new EMFModel with metamodel " + getMetaModelResource());

			EMFModel generatedEMFModel = null;

			try {

				EMFModelResource initialResource = getInitialResource(evaluationContext);
				logger.info("InitialResource=" + initialResource);

				EMFModelResource generatedResource = null;

				EMFTechnologyAdapter emfTA = getServiceManager().getTechnologyAdapterService()
						.getTechnologyAdapter(EMFTechnologyAdapter.class);

				if (getMetaModelResource() instanceof JarBasedMetaModelResource) {
					generatedResource = createResource(emfTA, EMFModelResourceFactory.class, evaluationContext,
							((JarBasedMetaModelResource) getMetaModelResource()).getModelFileExtension(), false);
				}
				else {
					generatedResource = createResource(emfTA, EMFModelResourceFactory.class, evaluationContext,
							EMFModelResource.XMI_EXTENSION, false);
				}
				generatedResource.setMetaModelResource(getMetaModelResource());
				System.out.println("Return new EMF resource: " + generatedResource);

				// Now copy file
				File destinationFile = (File) generatedResource.getIODelegate().getSerializationArtefact();
				System.out.println("destinationFile=" + destinationFile);
				generatedResource.getIODelegate().willWriteOnDisk();
				FileUtils.copyResourceToFile(initialResource.getIODelegate().getSerializationArtefactAsResource(), destinationFile);
				generatedResource.getIODelegate().hasWrittenOnDisk(null);

				// And load the new model
				generatedEMFModel = generatedResource.getResourceData();
				System.out.println("generatedEMFModel=" + generatedEMFModel);

			}

			catch (Exception e) {
				e.printStackTrace();
				throw new FMLExecutionException(e);
			}

			return generatedEMFModel;
		}
	}
}
