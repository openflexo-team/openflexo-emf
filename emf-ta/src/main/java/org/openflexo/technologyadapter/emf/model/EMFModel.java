/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012, THALES SYSTEMES AEROPORTES - All Rights Reserved
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

package org.openflexo.technologyadapter.emf.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.openflexo.foundation.ontology.FlexoOntologyObjectImpl;
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.IFlexoOntologyAnnotation;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyContainer;
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyDataType;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.metamodel.EMFClassClass;
import org.openflexo.technologyadapter.emf.metamodel.EMFEnumClass;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.io.EMFModelConverter;
import org.openflexo.technologyadapter.emf.rm.EMFModelResource;

/**
 * EMF Model.
 * 
 * @author gbesancon
 */
public class EMFModel extends FlexoOntologyObjectImpl<EMFTechnologyAdapter>
		implements FlexoModel<EMFModel, EMFMetaModel>, IFlexoOntology<EMFTechnologyAdapter> {

	protected static final Logger logger = Logger.getLogger(EMFModel.class.getPackage().getName());

	/** Resource. */
	protected EMFModelResource modelResource;
	/** MetaModel. */
	protected final EMFMetaModel metaModel;
	/** Converter. */
	protected final EMFModelConverter converter;
	/** Resource EMF. */
	protected final Resource resource;

	/**
	 * Constructor.
	 * 
	 * @param object
	 */
	public EMFModel(EMFMetaModel metaModel, EMFModelConverter converter, Resource resource) {
		this.metaModel = metaModel;
		this.converter = converter;
		this.resource = resource;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyObject#getName()
	 */
	@Override
	public String getName() {
		return EMFModelURIBuilder.getName(resource);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyObject#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) throws Exception {
		System.out.println("Name can't be modified.");
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyObject#getURI()
	 */
	@Override
	public String getURI() {
		if (getResource() != null) {
			return getResource().getURI();
		}
		return EMFModelURIBuilder.getUri(resource);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getVersion()
	 */
	@Override
	public String getVersion() {
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyObject#getDescription()
	 */
	@Override
	public String getDescription() {
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.FlexoOntologyObjectImpl#getDisplayableDescription()
	 */
	@Override
	public String getDisplayableDescription() {
		return getDescription();
	}

	/**
	 * Getter of converter.
	 * 
	 * @return the converter value
	 */
	public EMFModelConverter getConverter() {
		return converter;
	}

	/**
	 * Return the resource, as a {@link EMFModelResource}
	 */
	@Override
	public EMFModelResource getResource() {
		return modelResource;
	}

	@Override
	public void setResource(org.openflexo.foundation.resource.FlexoResource<EMFModel> resource) {
		modelResource = (EMFModelResource) resource;
	}

	/**
	 * Get EMF Resource.
	 * 
	 * @return
	 */
	public Resource getEMFResource() {
		return this.resource;
	}

	public void save() throws SaveResourceException {
		getResource().save();
	}

	/**
	 * Getter of metaModel.
	 * 
	 * @return the metaModel value
	 */
	@Override
	public EMFMetaModel getMetaModel() {
		return metaModel;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getRootConcept()
	 */
	@Override
	public IFlexoOntologyClass<EMFTechnologyAdapter> getRootConcept() {
		return metaModel.getRootConcept();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getImportedOntologies()
	 */
	@Override
	public List<IFlexoOntology<EMFTechnologyAdapter>> getImportedOntologies() {
		return Collections.singletonList((IFlexoOntology<EMFTechnologyAdapter>) metaModel);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getSubContainers()
	 */
	@Override
	public List<IFlexoOntologyContainer<EMFTechnologyAdapter>> getSubContainers() {
		return Collections.emptyList();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.FlexoModel#getObject(java.lang.String)
	 */
	@Override
	public Object getObject(String objectURI) {
		return getOntologyObject(objectURI);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getOntologyObject(java.lang.String)
	 */
	@Override
	public IFlexoOntologyConcept<EMFTechnologyAdapter> getOntologyObject(String objectURI) {
		IFlexoOntologyConcept<EMFTechnologyAdapter> result = null;
		for (IFlexoOntologyConcept<EMFTechnologyAdapter> concept : getConcepts()) {
			if (concept.getURI().equalsIgnoreCase(objectURI)) {
				result = concept;
			}
		}
		return result;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getDeclaredOntologyObject(java.lang.String)
	 */
	@Override
	public IFlexoOntologyConcept<EMFTechnologyAdapter> getDeclaredOntologyObject(String objectURI) {
		return getOntologyObject(objectURI);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getAnnotations()
	 */
	@Override
	public List<IFlexoOntologyAnnotation> getAnnotations() {
		return Collections.emptyList();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getDataTypes()
	 */
	@Override
	public List<IFlexoOntologyDataType<EMFTechnologyAdapter>> getDataTypes() {
		return Collections.emptyList();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getConcepts()
	 */
	@Override
	public List<IFlexoOntologyConcept<EMFTechnologyAdapter>> getConcepts() {
		List<IFlexoOntologyConcept<EMFTechnologyAdapter>> concepts = new ArrayList<>();
		concepts.addAll(getIndividuals());
		return Collections.unmodifiableList(concepts);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getClasses()
	 */
	@Override
	public List<? extends IFlexoOntologyClass<EMFTechnologyAdapter>> getClasses() {
		return Collections.emptyList();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getClass(java.lang.String)
	 */
	@Override
	public IFlexoOntologyClass<EMFTechnologyAdapter> getClass(String classURI) {
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getDeclaredClass(java.lang.String)
	 */
	@Override
	public IFlexoOntologyClass<EMFTechnologyAdapter> getDeclaredClass(String classURI) {
		return getClass(classURI);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getAccessibleClasses()
	 */
	@Override
	public List<? extends IFlexoOntologyClass<EMFTechnologyAdapter>> getAccessibleClasses() {
		return getClasses();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getIndividuals()
	 */
	@Override
	public List<EMFObjectIndividual> getIndividuals() {
		List<EMFObjectIndividual> result = new ArrayList<>();
		result.addAll(converter.getIndividuals().values());
		return Collections.unmodifiableList(result);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getIndividual(java.lang.String)
	 */
	@Override
	public EMFObjectIndividual getIndividual(String individualURI) {
		EMFObjectIndividual result = null;
		for (EMFObjectIndividual individual : getIndividuals()) {
			if (individual.getURI().equalsIgnoreCase(individualURI)) {
				result = individual;
			}
		}
		return result;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getDeclaredIndividual(java.lang.String)
	 */
	@Override
	public EMFObjectIndividual getDeclaredIndividual(String individualURI) {
		return getIndividual(individualURI);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getAccessibleIndividuals()
	 */
	@Override
	public List<EMFObjectIndividual> getAccessibleIndividuals() {
		return getIndividuals();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getProperty(java.lang.String)
	 */
	@Override
	public IFlexoOntologyStructuralProperty<EMFTechnologyAdapter> getProperty(String objectURI) {
		IFlexoOntologyStructuralProperty<EMFTechnologyAdapter> result = getDataProperty(objectURI);
		if (result == null) {
			result = getObjectProperty(objectURI);
		}
		return result;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getDeclaredProperty(java.lang.String)
	 */
	@Override
	public IFlexoOntologyStructuralProperty<EMFTechnologyAdapter> getDeclaredProperty(String objectURI) {
		return getProperty(objectURI);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getDataProperties()
	 */
	@Override
	public List<? extends IFlexoOntologyDataProperty<EMFTechnologyAdapter>> getDataProperties() {
		return Collections.emptyList();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getDataProperty(java.lang.String)
	 */
	@Override
	public IFlexoOntologyDataProperty<EMFTechnologyAdapter> getDataProperty(String propertyURI) {
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getDeclaredDataProperty(java.lang.String)
	 */
	@Override
	public IFlexoOntologyDataProperty<EMFTechnologyAdapter> getDeclaredDataProperty(String propertyURI) {
		return getDataProperty(propertyURI);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getAccessibleDataProperties()
	 */
	@Override
	public List<? extends IFlexoOntologyDataProperty<EMFTechnologyAdapter>> getAccessibleDataProperties() {
		return getDataProperties();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getObjectProperties()
	 */
	@Override
	public List<? extends IFlexoOntologyObjectProperty<EMFTechnologyAdapter>> getObjectProperties() {
		return Collections.emptyList();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getObjectProperty(java.lang.String)
	 */
	@Override
	public IFlexoOntologyObjectProperty<EMFTechnologyAdapter> getObjectProperty(String propertyURI) {
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getDeclaredObjectProperty(java.lang.String)
	 */
	@Override
	public IFlexoOntologyObjectProperty<EMFTechnologyAdapter> getDeclaredObjectProperty(String propertyURI) {
		return getObjectProperty(propertyURI);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getAccessibleObjectProperties()
	 */
	@Override
	public List<? extends IFlexoOntologyObjectProperty<EMFTechnologyAdapter>> getAccessibleObjectProperties() {
		return getObjectProperties();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.FlexoModel#getTechnologyAdapter()
	 */
	@Override
	public EMFTechnologyAdapter getTechnologyAdapter() {
		return metaModel.getTechnologyAdapter();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.FlexoOntologyObjectImpl#getFlexoOntology()
	 */
	@Override
	public IFlexoOntology<EMFTechnologyAdapter> getFlexoOntology() {
		return this;
	}

	@Override
	public String toString() {
		return "EMFModel:" + getURI();
	}

	/**
	 * Return a list of individuals of supplied type
	 * 
	 * TODO: i think this method is really sub-optimal and requires caching strategy implementation
	 * 
	 * @param type
	 * @return
	 */
	public List<EMFObjectIndividual> getIndividuals(IFlexoOntologyClass<?> type) {

		// System.out.println(
		// "Selecting EMFObjectIndividuals in " + getModelSlotInstance(evaluationContext).getModel() + " with type=" + getType());
		List<EMFObjectIndividual> selectedIndividuals = new ArrayList<>();
		Resource resource = getEMFResource();
		/*try {
			resource.load(null);
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		IFlexoOntologyClass<?> flexoOntologyClass = type;
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
				EMFClassClass emfObjectIndividualType = getMetaModel().getConverter().getClasses().get(eObject.eClass());

				// System.out.println("*** Found " + eObject + " type=" + emfObjectIndividualType + " flexoOntologyClass="
				// + flexoOntologyClass + " equals=" + (emfObjectIndividualType.equals(flexoOntologyClass)));

				if (emfObjectIndividualType != null && emfObjectIndividualType.equals(flexoOntologyClass)
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
			EMFObjectIndividual emfObjectIndividual = getConverter().getIndividuals().get(eObject);
			if (emfObjectIndividual != null) {
				selectedIndividuals.add(emfObjectIndividual);
			}
			else {
				logger.warning("It's weird there shoud be an existing OpenFlexo wrapper existing for EMF Object : " + eObject.toString());
				selectedIndividuals.add(getConverter().convertObjectIndividual(this, eObject));
			}
		}

		return selectedIndividuals;

	}

}
