 /*  Licensed to the Apache Software Foundation (ASF) under one or more
  *  contributor license agreements.  See the NOTICE file distributed with
  *  this work for additional information regarding copyright ownership.
  *  The ASF licenses this file to You under the Apache License, Version 2.0
  *  (the "License"); you may not use this file except in compliance with
  *  the License.  You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  *  Unless required by applicable law or agreed to in writing, software
  *  distributed under the License is distributed on an "AS IS" BASIS,
  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  *  See the License for the specific language governing permissions and
  *  limitations under the License.
  */
 package org.apache.aries.jpa.container.itest;
 
 import static org.ops4j.pax.exam.CoreOptions.equinox;
 import static org.ops4j.pax.exam.CoreOptions.options;
 import static org.ops4j.pax.exam.CoreOptions.systemProperty;
 import static org.ops4j.pax.exam.CoreOptions.wrappedBundle;
 import static org.ops4j.pax.exam.OptionUtils.combine;
 
 import java.util.Hashtable;
 
 import javax.persistence.EntityManagerFactory;
 import javax.persistence.spi.PersistenceProvider;
 
 import org.junit.Before;
 import org.junit.Ignore;
 import org.junit.Test;
 import org.junit.runner.RunWith;
 import org.ops4j.pax.exam.CoreOptions;
 import org.ops4j.pax.exam.Inject;
 import org.ops4j.pax.exam.Option;
 import org.ops4j.pax.exam.junit.JUnit4TestRunner;
 import org.ops4j.pax.exam.options.BootDelegationOption;
 import org.ops4j.pax.exam.options.MavenArtifactProvisionOption;
 import org.osgi.framework.Bundle;
 import org.osgi.framework.BundleContext;
 import org.osgi.framework.Constants;
 import org.osgi.framework.Filter;
 import org.osgi.framework.FrameworkUtil;
 import org.osgi.framework.InvalidSyntaxException;
 import org.osgi.framework.Version;
 import org.osgi.service.packageadmin.PackageAdmin;
 import org.osgi.util.tracker.ServiceTracker;
 
 @RunWith(JUnit4TestRunner.class)
 public class JPAContainerTest {
   public static final long DEFAULT_TIMEOUT = 30000;
 
   @Inject
   protected BundleContext bundleContext;
  
   @Before
   public void setupApplication() throws Exception
   {
    Bundle openJPA = getBundle("org.apache.openjpa");
    
    Class<? extends PersistenceProvider> clz = openJPA.loadClass("org.apache.openjpa.persistence.PersistenceProviderImpl");
    
    PersistenceProvider provider = clz.newInstance();
    
    Hashtable props = new Hashtable();
    
    props.put("javax.persistence.provider", "org.apache.openjpa.persistence.PersistenceProviderImpl");
    
    openJPA.getBundleContext().registerService(PersistenceProvider.class.getName(), provider, props);
    
     //Wait for everything to be started then refresh the app
     Thread.sleep(3000);
     
     Bundle app = getBundle("org.apache.aries.jpa.jpa-container-testbundle");
     
     PackageAdmin pa = getOsgiService(PackageAdmin.class);
     pa.refreshPackages(new Bundle[] {app});
   }
   
  //This test will run once there is an updated OpenJPA bundle that uses the latest JPA API
   @Test
   public void findEntityManagerFactory() throws Exception {
     EntityManagerFactory emf = getOsgiService(EntityManagerFactory.class, "(osgi.unit.name=test-unit)", DEFAULT_TIMEOUT);
   }
 
   @org.ops4j.pax.exam.junit.Configuration
   public static Option[] configuration() {
     Option[] options = options(
         bootDelegation(),
         
         // Log
         mavenBundle("org.ops4j.pax.logging", "pax-logging-api"),
         mavenBundle("org.ops4j.pax.logging", "pax-logging-service"),
         // Felix Config Admin
         mavenBundle("org.apache.felix", "org.apache.felix.configadmin"),
         // Felix mvn url handler
         mavenBundle("org.ops4j.pax.url", "pax-url-mvn"),
 
         // this is how you set the default log level when using pax
         // logging (logProfile)
         systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("DEBUG"),
 
         // Bundles
         mavenBundle("org.apache.aries.application", "org.apache.aries.application.utils"),
         mavenBundle("org.apache.aries.application", "org.apache.aries.application.api"),
         mavenBundle("org.osgi", "org.osgi.compendium"),
         mavenBundle("org.apache.aries", "org.apache.aries.util"),
         mavenBundle("org.apache.aries.blueprint", "org.apache.aries.blueprint"), 
         mavenBundle("org.apache.geronimo.specs", "geronimo-jpa_2.0_spec"),
         mavenBundle("org.apache.aries.jpa", "jpa-container"),
         mavenBundle("org.apache.geronimo.specs", "geronimo-jta_1.1_spec"),
         mavenBundle("commons-lang", "commons-lang"),
         mavenBundle("commons-collections", "commons-collections"),
         mavenBundle("commons-pool", "commons-pool"),
         mavenBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.serp"),
         mavenBundle("org.apache.openjpa", "openjpa"),
 
 //        mavenBundle("org.eclipse.persistence", "org.eclipse.persistence.jpa"),
 //        mavenBundle("org.eclipse.persistence", "org.eclipse.persistence.core"),
 //        mavenBundle("org.eclipse.persistence", "org.eclipse.persistence.asm"),
         
         mavenBundle("org.apache.aries.jpa", "jpa-container-testbundle"),
         
         equinox().version("3.5.0"));
     options = updateOptions(options);
     return options;
   }
   
   
   protected Bundle getBundle(String symbolicName) {
     return getBundle(symbolicName, null);
   }
 
   protected Bundle getBundle(String bundleSymbolicName, String version) {
     Bundle result = null;
     for (Bundle b : bundleContext.getBundles()) {
       if (b.getSymbolicName().equals(bundleSymbolicName)) {
         if (version == null
             || b.getVersion().equals(Version.parseVersion(version))) {
           result = b;
           break;
         }
       }
     }
     return result;
   }
 
   public static BootDelegationOption bootDelegation() {
     return new BootDelegationOption("org.apache.aries.unittest.fixture");
   }
   
   public static MavenArtifactProvisionOption mavenBundle(String groupId,
       String artifactId) {
     return CoreOptions.mavenBundle().groupId(groupId).artifactId(artifactId)
         .versionAsInProject();
   }
 
   protected static Option[] updateOptions(Option[] options) {
     // We need to add pax-exam-junit here when running with the ibm
     // jdk to avoid the following exception during the test run:
     // ClassNotFoundException: org.ops4j.pax.exam.junit.Configuration
     if ("IBM Corporation".equals(System.getProperty("java.vendor"))) {
       Option[] ibmOptions = options(wrappedBundle(mavenBundle(
           "org.ops4j.pax.exam", "pax-exam-junit")));
       options = combine(ibmOptions, options);
     }
 
     return options;
   }
 
   protected <T> T getOsgiService(Class<T> type, long timeout) {
     return getOsgiService(type, null, timeout);
   }
 
   protected <T> T getOsgiService(Class<T> type) {
     return getOsgiService(type, null, DEFAULT_TIMEOUT);
   }
   
   protected <T> T getOsgiService(Class<T> type, String filter, long timeout) {
     return getOsgiService(null, type, filter, timeout);
   }
 
   protected <T> T getOsgiService(BundleContext bc, Class<T> type,
       String filter, long timeout) {
     ServiceTracker tracker = null;
     try {
       String flt;
       if (filter != null) {
         if (filter.startsWith("(")) {
           flt = "(&(" + Constants.OBJECTCLASS + "=" + type.getName() + ")"
               + filter + ")";
         } else {
           flt = "(&(" + Constants.OBJECTCLASS + "=" + type.getName() + ")("
               + filter + "))";
         }
       } else {
         flt = "(" + Constants.OBJECTCLASS + "=" + type.getName() + ")";
       }
       Filter osgiFilter = FrameworkUtil.createFilter(flt);
       tracker = new ServiceTracker(bc == null ? bundleContext : bc, osgiFilter,
           null);
       tracker.open();
       // Note that the tracker is not closed to keep the reference
       // This is buggy, has the service reference may change i think
       Object svc = type.cast(tracker.waitForService(timeout));
       if (svc == null) {
         throw new RuntimeException("Gave up waiting for service " + flt);
       }
       return type.cast(svc);
     } catch (InvalidSyntaxException e) {
       throw new IllegalArgumentException("Invalid filter", e);
     } catch (InterruptedException e) {
       throw new RuntimeException(e);
     }
   }
 }
