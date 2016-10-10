package com.wijdemans;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

public class ImmediateFeature implements Feature{

    private static final Logger logger = LoggerFactory.getLogger(ImmediateFeature.class);

    @Inject
    public ImmediateFeature(ServiceLocator locator) {
        logger.debug("Enable immediate scope");
        ServiceLocatorUtilities.enableImmediateScope(locator);
    }

    @Override
    public boolean configure(FeatureContext context) {
        return true;
    }
}
