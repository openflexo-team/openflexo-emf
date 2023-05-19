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

import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.connie.type.ProxyType;
import org.openflexo.foundation.fml.editionaction.AbstractFetchRequest;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.foundation.ontology.fml.editionaction.AbstractSelectIndividual;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.technologyadapter.emf.EMFModelSlot;
import org.openflexo.technologyadapter.emf.EMFObjectIndividualType;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividual;

/**
 * A generic {@link AbstractFetchRequest} allowing to retrieve a selection of some {@link EMFObjectIndividual} matching some conditions and
 * a given type.<br>
 * 
 * @author sylvain
 */
@ModelEntity(isAbstract = true)
public interface AbstractSelectEMFObjectIndividual<AT> extends AbstractSelectIndividual<EMFModelSlot, EMFModel, EMFObjectIndividual, AT> {

	public static abstract class AbstractSelectEMFObjectIndividualImpl<AT> extends
			AbstractSelectIndividualImpl<EMFModelSlot, EMFModel, EMFObjectIndividual, AT> implements AbstractSelectEMFObjectIndividual<AT> {

		private static final Logger logger = Logger.getLogger(AbstractSelectEMFObjectIndividual.class.getPackage().getName());

		@Override
		public void setFetchedType(Type type) {
			super.setFetchedType(type);
			if (type instanceof EMFObjectIndividualType) {
				setType(((EMFObjectIndividualType) type).getOntologyClass());
			}
			if (type instanceof ProxyType && ((ProxyType) type).getReferencedType() instanceof EMFObjectIndividualType) {
				setType(((EMFObjectIndividualType) ((ProxyType) type).getReferencedType()).getOntologyClass());
			}
		}

		@Override
		public List<EMFObjectIndividual> performExecute(RunTimeEvaluationContext evaluationContext) {
			EMFModel emfModel = getReceiver(evaluationContext);

			if (emfModel == null) {
				logger.warning("Cannot retrieve receiver " + getReceiver());
				return null;
			}

			List<EMFObjectIndividual> selectedIndividuals;
			if (getType() == null) {
				selectedIndividuals = (List) emfModel.getIndividuals();
			}
			else {
				selectedIndividuals = (List) emfModel.getIndividuals(getType());
			}

			List<EMFObjectIndividual> returned = filterWithConditions(selectedIndividuals, evaluationContext);
			return returned;

		}

	}
}
