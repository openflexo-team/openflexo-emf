/**
 * 
 * Copyright (c) 2014-2015, Openflexo
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

package org.openflexo.technologyadapter.emf.fml.editionaction;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.openflexo.foundation.fml.editionaction.AbstractFetchRequest;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.fml.editionaction.AbstractSelectIndividual;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.technologyadapter.emf.EMFModelSlot;
import org.openflexo.technologyadapter.emf.metamodel.EMFClassClass;
import org.openflexo.technologyadapter.emf.metamodel.EMFEnumClass;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividual;

/**
 * A generic {@link AbstractFetchRequest} allowing to retrieve a selection of some {@link EMFObjectIndividual} matching some conditions and
 * a given type.<br>
 * 
 * @author sylvain
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(AbstractSelectEMFObjectIndividual.SelectEMFObjectIndividualImpl.class)
public interface AbstractSelectEMFObjectIndividual<AT> extends AbstractSelectIndividual<EMFModelSlot, EMFModel, EMFObjectIndividual, AT> {

	public static abstract class SelectEMFObjectIndividualImpl<AT> extends
			AbstractSelectIndividualImpl<EMFModelSlot, EMFModel, EMFObjectIndividual, AT> implements AbstractSelectEMFObjectIndividual<AT> {

		private static final Logger logger = Logger.getLogger(AbstractSelectEMFObjectIndividual.class.getPackage().getName());

		@Override
		public List<EMFObjectIndividual> performExecute(RunTimeEvaluationContext evaluationContext) {
			EMFModel emfModel = getReceiver(evaluationContext);

			// System.out.println(
			// "Selecting EMFObjectIndividuals in " + getModelSlotInstance(evaluationContext).getModel() + " with type=" + getType());
			List<EMFObjectIndividual> selectedIndividuals = new ArrayList<>();
			Resource resource = emfModel.getEMFResource();
			/*try {
				resource.load(null);
			} catch (IOException e) {
				e.printStackTrace();
			}*/
			IFlexoOntologyClass<?> flexoOntologyClass = getType();
			List<EObject> selectedEMFIndividuals = new ArrayList<>();
			if (flexoOntologyClass instanceof EMFClassClass) {
				TreeIterator<EObject> iterator = resource.getAllContents();
				while (iterator.hasNext()) {
					EObject eObject = iterator.next();
					// FIXME: following commented code was written by gilles
					// Seems to not working
					// Replaced by following
					// Gilles, could you check and explain ?
					/*selectedEMFIndividuals.addAll(EcoreUtility.getAllContents(eObject, ((EMFClassClass) flexoOntologyClass).getObject()
							.getClass()));*/
					EMFClassClass emfObjectIndividualType = emfModel.getMetaModel().getConverter().getClasses().get(eObject.eClass());

					// System.out.println("*** Found " + eObject + " type=" + emfObjectIndividualType + " flexoOntologyClass="
					// + flexoOntologyClass + " equals=" + (emfObjectIndividualType.equals(flexoOntologyClass)));

					if (emfObjectIndividualType.equals(flexoOntologyClass)
							|| ((EMFClassClass) flexoOntologyClass).isSuperClassOf(emfObjectIndividualType)) {
						selectedEMFIndividuals.add(eObject);
					}
				}
			}
			else if (flexoOntologyClass instanceof EMFEnumClass) {
				System.err.println(
						"We shouldn't browse enum individuals of type " + ((EMFEnumClass) flexoOntologyClass).getObject().getName() + ".");
			}

			// System.out.println("selectedEMFIndividuals=" + selectedEMFIndividuals);

			for (EObject eObject : selectedEMFIndividuals) {
				EMFObjectIndividual emfObjectIndividual = emfModel.getConverter().getIndividuals().get(eObject);
				if (emfObjectIndividual != null) {
					selectedIndividuals.add(emfObjectIndividual);
				}
				else {
					logger.warning(
							"It's weird there shoud be an existing OpenFlexo wrapper existing for EMF Object : " + eObject.toString());
					selectedIndividuals.add(emfModel.getConverter().convertObjectIndividual(emfModel, eObject));
				}
			}

			List<EMFObjectIndividual> returned = filterWithConditions(selectedIndividuals, evaluationContext);

			// System.out.println("SelectEMFObjectIndividual, without filtering =" + selectedIndividuals + " after filtering=" + returned);

			return returned;

		}

	}
}
