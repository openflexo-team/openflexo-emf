/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.technologyadapter.emf.rm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.resource.Resource;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FileWritingLock;
import org.openflexo.foundation.resource.FlexoResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.resource.SaveResourcePermissionDeniedException;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.model.io.EMFModelConverter;

/**
 * Represents the resource associated to a {@link OWLOntology}
 * 
 * @author sguerin
 * 
 */
public abstract class EMFModelResourceImpl extends FlexoResourceImpl<EMFModel> implements EMFModelResource {

	private static final Logger logger = Logger.getLogger(EMFModelResourceImpl.class.getPackage().getName());

	/** Model Resource. */
	protected Resource modelResource;

	/**
	 * Load the &quot;real&quot; load resource data of this resource.
	 * 
	 * @param progress
	 *            a progress monitor in case the resource data is not immediately available.
	 * @return the resource data.
	 * @throws ResourceLoadingCancelledException
	 * @throws ResourceDependencyLoopException
	 * @throws FileNotFoundException
	 * @throws FlexoException
	 */
	@Override
	public EMFModel loadResourceData() throws ResourceLoadingCancelledException, FileNotFoundException, FlexoException {

		EMFModelConverter converter = new EMFModelConverter();
		EMFModel resourceData;
		resourceData = converter.convertModel(getMetaModelResource().getMetaModelData(), getEMFResource());
		setResourceData(resourceData);
		return resourceData;
	}

	/**
	 * Save the &quot;real&quot; resource data of this resource.
	 * 
	 * @throws SaveResourceException
	 */
	@Override
	public void save() throws SaveResourceException {
		EMFModel resourceData;
		try {
			resourceData = getResourceData();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new SaveResourceException(getIODelegate());
		} catch (ResourceLoadingCancelledException e) {
			e.printStackTrace();
			throw new SaveResourceException(getIODelegate());
		} catch (FlexoException e) {
			e.printStackTrace();
			throw new SaveResourceException(getIODelegate());
		}

		if (!getIODelegate().hasWritePermission()) {
			if (logger.isLoggable(Level.WARNING)) {
				// logger.warning("Permission denied : " + getFile().getAbsolutePath());
				logger.warning("Permission denied : " + getIODelegate().toString());
			}
			throw new SaveResourcePermissionDeniedException(getIODelegate());
		}
		if (resourceData != null) {
			FileWritingLock lock = getIODelegate().willWriteOnDisk();
			writeToFile();
			getIODelegate().hasWrittenOnDisk(lock);
			notifyResourceStatusChanged();
			resourceData.clearIsModified(false);
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Succeeding to save Resource " + getURI() + " : " + getIODelegate().toString());
			}
		}
	}

	@Override
	public EMFModel getModelData() {
		try {
			return getResourceData();
		} catch (ResourceLoadingCancelledException e) {
			e.printStackTrace();
			return null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (FlexoException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public EMFModel getModel() {
		return getModelData();
	}

	/**
	 * Write file.
	 * 
	 * @throws SaveResourceException
	 */
	private void writeToFile() throws SaveResourceException {
		try {
			getEMFResource().save(null);
			logger.info("Wrote " + getIODelegate().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Getter of EMF Model Resource.
	 * 
	 * @return the modelResource value
	 */
	@Override
	public Resource getEMFResource() {
		if (modelResource == null) {
			EMFMetaModelResource mmResource = (EMFMetaModelResource) getMetaModelResource();
			if (mmResource == null) {
				logger.warning("EMFModel has no meta-model !!!");
				return null;
			}
			else {
				if (!mmResource.isLoaded()) {
					try {
						mmResource.loadResourceData();
					} catch (FileNotFoundException e) {
						logger.warning("Cannot load EMF MetaModel");
						return null;
					} catch (ResourceLoadingCancelledException e) {
						logger.warning("Cannot load EMF MetaModel");
						return null;
					} catch (FlexoException e) {
						logger.warning("Cannot load EMF MetaModel");
						return null;
					}
				}

			}

			// TODO: should be refactored with IODelegates Also (BE AWARE THAT FOR EMF, THE METAMODEL DECIDES WHO IS CREATING THE
			// RESOURCES!!
			modelResource = mmResource.createEMFModelResource(getIODelegate());

		}
		return modelResource;
	}

	@Override
	public Class<EMFModel> getResourceDataClass() {
		return EMFModel.class;
	}

}
