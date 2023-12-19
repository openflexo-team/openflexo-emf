/**
 * 
 * Copyright (c) 2013-2015, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
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
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.InnerResourceData;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.resource.FlexoResourceImpl;
import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.metamodel.AEMFMetaModelObjectImpl;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;

/**
 * Default abstract implementation for a {@link EMFMetaModelResource}
 * 
 * @author xtof, sylvain
 */
public abstract class EMFMetaModelResourceImpl extends FlexoResourceImpl<EMFMetaModel> implements EMFMetaModelResource {

	protected static final Logger logger = Logger.getLogger(EMFMetaModelResourceImpl.class.getPackage().getName());

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.technologyadapter.emf.rm.EMFMetaModelResource#getMetaModel()
	 */
	@Override
	public final EMFMetaModel getMetaModelData() {

		try {
			return getResourceData();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ResourceLoadingCancelledException e) {
			e.printStackTrace();
		} catch (FlexoException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public final EMFMetaModel getMetaModel() {
		return getMetaModelData();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.resource.FlexoResource#save()
	 */
	@Override
	public void save() {
		logger.info("MetaModel is not supposed to be modified.");
	}

	@Override
	public Class<EMFMetaModel> getResourceDataClass() {
		return EMFMetaModel.class;
	}

	/**
	 * Generic method used to retrieve in this resource an object with supplied objectIdentifier, userIdentifier, and type identifier<br>
	 * 
	 * Note that for certain resources, some parameters might not be used (for example userIdentifier or typeIdentifier)
	 * 
	 * @param objectIdentifier
	 * @param userIdentifier
	 * @param typeIdentifier
	 * @return
	 */
	@Override
	public IFlexoOntologyConcept<EMFTechnologyAdapter> findObject(String objectIdentifier, String userIdentifier, String typeIdentifier) {
		System.out.println("Dans EMFMetaModelResource, on me demande de trouver l'objet objectIdentifier=" + objectIdentifier
				+ " userIdentifier=" + userIdentifier + " typeIdentifier=" + typeIdentifier);

		EMFMetaModel metaModel = getMetaModel();

		IFlexoOntologyConcept<EMFTechnologyAdapter> returned = metaModel.getClass(objectIdentifier);
		if (returned != null) {
			return returned;
		}
		returned = metaModel.getOntologyObject(objectIdentifier);
		return returned;
	}

	/**
	 * Generic method used to retrieve in this resource an object with supplied objectIdentifier, userIdentifier, and type identifier<br>
	 * 
	 * @param objectIdentifier
	 * @param userIdentifier
	 * @return
	 */
	@Override
	public IFlexoOntologyConcept<EMFTechnologyAdapter> findObject(String objectIdentifier, String userIdentifier) {
		return findObject(objectIdentifier, userIdentifier, null);
	}

	/**
	 * Used to compute identifier of an object asserting this object is the {@link ResourceData} itself, or a {@link InnerResourceData}
	 * object stored inside this resource
	 * 
	 * @param object
	 * @return a String identifying supplied object (semantics is composite key using userIdentifier and typeIdentifier)
	 */
	@Override
	public String getObjectIdentifier(Object object) {

		if (object instanceof IFlexoOntologyConcept) {
			return ((IFlexoOntologyConcept<?>) object).getURI();
		}
		if (object instanceof AEMFMetaModelObjectImpl) {
			EObject eObject = ((AEMFMetaModelObjectImpl) object).getObject();
			return EcoreUtil.getID(eObject);
		}
		logger.warning("Unexpected object " + object);
		return null;
	}

	/**
	 * Used to compute user identifier of an object asserting this object is the {@link ResourceData} itself, or a {@link InnerResourceData}
	 * object stored inside this resource
	 * 
	 * @param object
	 * @return a String identifying author (user) of supplied object
	 */
	@Override
	public String getUserIdentifier(Object object) {
		return "FLX";
	}

}
