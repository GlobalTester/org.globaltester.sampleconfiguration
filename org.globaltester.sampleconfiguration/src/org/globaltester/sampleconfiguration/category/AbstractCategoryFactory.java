package org.globaltester.sampleconfiguration.category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.globaltester.sampleconfiguration.category.parameter.CategoryParameterDescription;

public abstract class AbstractCategoryFactory implements CategoryFactory {
	
	private String name;
	private String uiName;
	protected List<CategoryParameterDescription> paramDescriptions = new ArrayList<>();
	
	public AbstractCategoryFactory(String name) {
		this.name = name;
		this.uiName = name;
	}
	
	public AbstractCategoryFactory(String name, String uiName) {
		this.name = name;
		this.uiName = uiName;
	}
	
	public final void addParameterDescription(CategoryParameterDescription description){
		if (description == null){
			throw new IllegalArgumentException("The given description can not be null");
		}
		paramDescriptions.add(description);
	}
	
	@Override
	public List<CategoryParameterDescription> getParameterDescriptors(){
		return new ArrayList<>(paramDescriptions);
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getUiName() {
		return uiName;
	}
	
	@Override
	public boolean isUsable() {
		return true;
	}
	
	@Override
	public String toString() {
		return this.getName() + "[" + Arrays.toString(paramDescriptions.toArray()) + "]";
	}
}
