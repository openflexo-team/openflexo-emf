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

package org.openflexo.technologyadapter.emf.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.openflexo.foundation.ontology.IFlexoOntologyDataPropertyValue;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;

/**
 * EMF FeatureMap wrapper
 * 
 * @author sylvain
 */
public class EMFObjectIndividualAttributeDataPropertyValueAsFeatureMap extends EMFObjectIndividualAttributeDataPropertyValue implements
		IFlexoOntologyDataPropertyValue<EMFTechnologyAdapter>/*, Map<String, IFlexoOntologyPropertyValue<EMFTechnologyAdapter>>*/ {

	private static final Logger logger = Logger
			.getLogger(EMFObjectIndividualAttributeDataPropertyValueAsFeatureMap.class.getPackage().getName());

	private FeatureMap featureMap;
	private Map<String, Object> valuesMap;

	public EMFObjectIndividualAttributeDataPropertyValueAsFeatureMap(EMFModel model, EObject eObject, EAttribute eAttribute,
			FeatureMap featureMap) {
		super(model, eObject, eAttribute);
		this.featureMap = featureMap;

		valuesMap = new HashMap<>();

		for (int i = 0; i < featureMap.size(); i++) {
			org.eclipse.emf.ecore.util.FeatureMap.Entry entry = featureMap.get(i);

			EStructuralFeature eSF = entry.getEStructuralFeature();
			Object value = entry.getValue();

			System.out.println(eSF.getName() + " : " + eSF.getEType().getName() + " = " + entry.getValue());

			if (eSF instanceof EAttribute) {
				valuesMap.put(eSF.getName(), value);
			}
			else if (eSF instanceof EReference) {

				if (eSF.getUpperBound() == 1) {
					if (value instanceof EObject) {
						valuesMap.put(eSF.getName(), ontology.getConverter().convertObjectIndividual(model, (EObject) value));
					}
					else {
						valuesMap.put(eSF.getName(), value);
					}
				}
				else {
					if (value instanceof EObject) {
						valuesMap.put(eSF.getName(), ontology.getConverter().convertObjectIndividual(model, (EObject) value));
					}
					else if (value instanceof EList) {
						List<Object> values = new ArrayList<>();
						for (Object aListValue : (EList<?>) value) {
							if (aListValue instanceof EObject) {
								values.add(ontology.getConverter().convertObjectIndividual(model, (EObject) value));
							}
							else {
								values.add(value);
								// logger.warning("Unexpected value : " + aListValue + " of " + aListValue.getClass());
							}
						}
						valuesMap.put(eSF.getName(), values);
					}
				}
				/*// Annotation content
				if (eObject instanceof EModelElement && eSF.getFeatureID() == EcorePackage.EMODEL_ELEMENT__EANNOTATIONS) {
					convertObjectIndividualAnnotations(model, individual, eObject, (EReference) eSF);
				}
				else {
					// Other References
					convertObjectIndividualReferenceObjectPropertyValue(model, eObject, (EReference) eSF);
				}*/

			}

		}
	}

	/*@Override
	public int size() {
		return featureMap.size();
	}
	
	@Override
	public boolean isEmpty() {
		return featureMap.isEmpty();
	}
	
	public EStructuralFeature getFeature(String featureName) {
		for (int i = 0; i < featureMap.size(); i++) {
			EStructuralFeature eStructuralFeature = featureMap.getEStructuralFeature(i);
			if (eStructuralFeature.getName().equals(featureName)) {
				return eStructuralFeature;
			}
		}
		return null;
	}
	
	@Override
	public boolean containsKey(Object key) {
		if (key instanceof String) {
			return getFeature((String) key) != null;
		}
		return false;
	}
	
	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public IFlexoOntologyPropertyValue<EMFTechnologyAdapter> get(Object key) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public IFlexoOntologyPropertyValue<EMFTechnologyAdapter> put(String key, IFlexoOntologyPropertyValue<EMFTechnologyAdapter> value) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public IFlexoOntologyPropertyValue<EMFTechnologyAdapter> remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void putAll(Map<? extends String, ? extends IFlexoOntologyPropertyValue<EMFTechnologyAdapter>> m) {
		// TODO Auto-generated method stub
	
	}
	
	@Override
	public void clear() {
		// TODO Auto-generated method stub
	
	}
	
	@Override
	public Set<String> keySet() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Collection<IFlexoOntologyPropertyValue<EMFTechnologyAdapter>> values() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Set<Entry<String, IFlexoOntologyPropertyValue<EMFTechnologyAdapter>>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}*/

}
