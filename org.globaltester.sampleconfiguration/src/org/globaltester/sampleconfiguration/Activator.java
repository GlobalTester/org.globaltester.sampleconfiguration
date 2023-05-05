package org.globaltester.sampleconfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.globaltester.sampleconfiguration.category.CategoryFactory;
import org.globaltester.sampleconfiguration.category.parameter.CategoryParameterDescription;
import org.globaltester.sampleconfiguration.category.parameter.SeparatorCategoryParameter;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Handles the category service
 * 
 * @author amay
 *
 */
public class Activator implements BundleActivator {

	public static final String PLUGIN_ID = "org.globaltester.sampleconfiguration";
	
	private static Activator defaultInstance;
	private static BundleContext context;
	private static ServiceTracker<CategoryFactory, CategoryFactory> factoryTracker;

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		start(bundleContext, this);
	}

	private static synchronized void start(BundleContext bundleContext, Activator instance) {
		context = bundleContext;
		defaultInstance = instance;
		if (factoryTracker == null) {
			factoryTracker = new ServiceTracker<>(context, CategoryFactory.class, null);
			factoryTracker.open();
		}
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		stop();
	}

	private static synchronized void stop() {
		if (factoryTracker != null) {
			factoryTracker.close();
			factoryTracker = null;
		}
		defaultInstance = null;
		context = null;
	}

	public static BundleContext getContext() {
		return context;
	}

	/**
	 * Returns a {@link Collection} of available {@link CategoryFactory}
	 * objects. One for each registered OSGi service.
	 * 
	 * @return
	 */
	public static CategoryFactory[] getAvailableCategoryFactories() {
		CategoryFactory[] emptyArray = new CategoryFactory[0];
		if (factoryTracker == null) {
			return emptyArray;
		}
		CategoryFactory[] services = factoryTracker.getServices(emptyArray);
		List<CategoryFactory> usable = new ArrayList<>();
		for (CategoryFactory c : services) {
			if (c.isUsable()) {
				usable.add(c);
			} else {
				usable.add(new CategoryFactory() {
					
					@Override
					public boolean isUsable() {
						return false;
					}
					
					@Override
					public String getUiName() {
						return c.getUiName();
					}
					
					@Override
					public List<CategoryParameterDescription> getParameterDescriptors() {
						List<CategoryParameterDescription> check = new ArrayList<CategoryParameterDescription>();
						check.add(new SeparatorCategoryParameter(c.getName(), "Usage preconditions for this feature not fullfilled", "Usage preconditions for this feature not fullfilled"));
						return check;
					}
					
					@Override
					public String getName() {
						return c.getName();
					}
				});
			}
		}
		return usable.toArray(emptyArray);
	}

	/**
	 * @return a {@link CategoryFactory} that produces Categorys with the
	 *         provided categoryName or null if none is available
	 */
	public static CategoryFactory getCategoryFactoriesForName(String categoryName) {
		if (categoryName == null) return null;
		
		
		CategoryFactory[] availableFactories = getAvailableCategoryFactories();
		
		for (CategoryFactory currentFactory : availableFactories) {
			if (categoryName.equals(currentFactory.getName())) {
				return currentFactory;
			}
	}

		return null;
	}

	public static Activator getDefault() {
		return defaultInstance;
	}
}
