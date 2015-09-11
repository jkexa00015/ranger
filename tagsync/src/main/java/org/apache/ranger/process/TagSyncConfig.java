/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.ranger.process;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public class TagSyncConfig extends Configuration {
	private static final Logger LOG = Logger.getLogger(TagSyncConfig.class) ;

	public static final String CONFIG_FILE = "ranger-tagsync-site.xml";

	public static final String DEFAULT_CONFIG_FILE = "ranger-tagsync-default.xml";

	public static final String TAGSYNC_ENABLED_PROP = "ranger.tagsync.enabled" ;

	public static final String TAGSYNC_PORT_PROP = "ranger.tagsync.port" ;

	public static final String TAGSYNC_SSL_PROP = "ranger.tagsync.ssl" ;

	public static final String TAGSYNC_LOGDIR_PROP = "ranger.tagsync.logdir" ;

	private static final String TAGSYNC_TAGADMIN_REST_URL_PROP = "ranger.tagsync.tagadmin.rest.url";

	private static final String TAGSYNC_TAGADMIN_REST_SSL_CONFIG_FILE_PROP = "ranger.tagsync.tagadmin.rest.ssl.config.file";

	private static final String TAGSYNC_PM_SSL_BASICAUTH_USERNAME_PROP = "ranger.tagsync.policymanager.basicauth.username";

	private static final String TAGSYNC_PM_SSL_BASICAUTH_PASSWORD_PROP = "ranger.tagsync.policymanager.basicauth.password";

	private static final String TAGSYNC_SOURCE_FILE_PROP = "ranger.tagsync.source.file";

	private static final String TAGSYNC_SLEEP_TIME_IN_MILLIS_BETWEEN_CYCLE_PROP = "ranger.tagsync.sleeptimeinmillisbetweensynccycle";

	private static final String TAGSYNC_SOURCE_CLASS_PROP = "ranger.tagsync.source.impl.class";

	private static final String TAGSYNC_SINK_CLASS_PROP = "ranger.tagsync.sink.impl.class";

	private static final String TAGSYNC_SOURCE_ATLAS_PROP = "atlas.endpoint";

	private static final String TAGSYNC_SERVICENAME_MAPPER_PROP_PREFIX = "ranger.tagsync.atlas.";

	private static final String TAGSYNC_SERVICENAME_MAPPER_PROP_SUFFIX = ".ranger.service";

	private static final String TAGSYNC_DEFAULT_CLUSTERNAME_AND_COMPONENTNAME_SEPARATOR = "_";

	private static volatile TagSyncConfig instance = null;

	public static TagSyncConfig getInstance() {
	/*
		TagSyncConfig ret = instance;
		if (ret == null) {
			synchronized(TagSyncConfig.class) {
				if (ret == null) {
					ret = instance = new TagSyncConfig();
					LOG.debug("TagSyncConfig = {" + ret + "}");
				}
			}
		}
	*/
		TagSyncConfig newConfig = new TagSyncConfig();
		return newConfig;
	}

	public Properties getProperties() {
		return getProps();
	}

	public static InputStream getFileInputStream(String path) throws FileNotFoundException {

		InputStream ret = null;

		File f = new File(path);

		if (f.exists() && f.isFile() && f.canRead()) {
			ret = new FileInputStream(f);
		} else {
			ret = TagSyncConfig.class.getResourceAsStream(path);

			if (ret == null) {
				if (! path.startsWith("/")) {
					ret = TagSyncConfig.class.getResourceAsStream("/" + path);
				}
			}

			if (ret == null) {
				ret = ClassLoader.getSystemClassLoader().getResourceAsStream(path) ;
				if (ret == null) {
					if (! path.startsWith("/")) {
						ret = ClassLoader.getSystemResourceAsStream("/" + path);
					}
				}
			}
		}

		return ret;
	}

	public static String getResourceFileName(String path) {

		String ret = null;

		if (StringUtils.isNotBlank(path)) {

			File f = new File(path);

			if (f.exists() && f.isFile() && f.canRead()) {
				ret = path;
			} else {

				URL fileURL = TagSyncConfig.class.getResource(path);
				if (fileURL == null) {
					if (!path.startsWith("/")) {
						fileURL = TagSyncConfig.class.getResource("/" + path);
					}
				}

				if (fileURL == null) {
					fileURL = ClassLoader.getSystemClassLoader().getResource(path);
					if (fileURL == null) {
						if (!path.startsWith("/")) {
							fileURL = ClassLoader.getSystemClassLoader().getResource("/" + path);
						}
					}
				}

				if (fileURL != null) {
					try {
						ret = fileURL.getFile();
					} catch (Exception exception) {
						LOG.error(path + " is not a file", exception);
					}
				} else {
					LOG.warn("URL not found for " + path + " or no privilege for reading file " + path);
				}
			}
		}

		return ret;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append("DEFAULT_CONFIG_FILE=").append(DEFAULT_CONFIG_FILE).append(", ")
				.append("CONFIG_FILE=").append(CONFIG_FILE).append("\n\n");

		return sb.toString() + super.toString();
	}

	static public boolean isTagSyncEnabled(Properties prop) {
		String val = prop.getProperty(TAGSYNC_ENABLED_PROP);
		return !(val != null && val.trim().equalsIgnoreCase("falae"));
	}

	static public String getTagSyncPort(Properties prop) {
		String val = prop.getProperty(TAGSYNC_PORT_PROP);
		return val;
	}

	static public boolean isTagSyncSsl(Properties prop) {
		String val = prop.getProperty(TAGSYNC_SSL_PROP);
		return (val != null && val.trim().equalsIgnoreCase("true"));
	}

	static public String getTagSyncLogdir(Properties prop) {
		String val = prop.getProperty(TAGSYNC_LOGDIR_PROP);
		return val;
	}

	static public long getSleepTimeInMillisBetweenCycle(Properties prop) {
		String val = prop.getProperty(TAGSYNC_SLEEP_TIME_IN_MILLIS_BETWEEN_CYCLE_PROP);
		return Long.valueOf(val);
	}

	static public String getTagSourceClassName(Properties prop) {
		String val = prop.getProperty(TAGSYNC_SOURCE_CLASS_PROP);
		return val;
	}

	static public String getTagSinkClassName(Properties prop) {
		String val = prop.getProperty(TAGSYNC_SINK_CLASS_PROP);
		return val;
	}

	static public String getTagAdminRESTUrl(Properties prop) {
		String val = prop.getProperty(TAGSYNC_TAGADMIN_REST_URL_PROP);
		return val;
	}

	static public String getTagAdminRESTSslConfigFile(Properties prop) {
		String val = prop.getProperty(TAGSYNC_TAGADMIN_REST_SSL_CONFIG_FILE_PROP);
		return val;
	}

	static public String getPolicyMgrUserName(Properties prop) {
		String val = prop.getProperty(TAGSYNC_PM_SSL_BASICAUTH_USERNAME_PROP);
		return val;
	}

	static public String getPolicyMgrPassword(Properties prop) {
		String val = prop.getProperty(TAGSYNC_PM_SSL_BASICAUTH_PASSWORD_PROP);
		return val;
	}

	static public String getTagSourceFileName(Properties prop) {
		String val = prop.getProperty(TAGSYNC_SOURCE_FILE_PROP);
		return val;
	}

	static public String getAtlasEndpoint(Properties prop) {
		String val = prop.getProperty(TAGSYNC_SOURCE_ATLAS_PROP);
		return val;
	}

	static public String getAtlasSslConfigFileName(Properties prop) {
		return "";
	}

	static public String getServiceName(String componentName, String instanceName, Properties prop) {
		String propName = TAGSYNC_SERVICENAME_MAPPER_PROP_PREFIX + componentName
				+ ".instance." + instanceName
				+ TAGSYNC_SERVICENAME_MAPPER_PROP_SUFFIX;
		String val = prop.getProperty(propName);
		if (StringUtils.isBlank(val)) {
			val = instanceName + TAGSYNC_DEFAULT_CLUSTERNAME_AND_COMPONENTNAME_SEPARATOR + componentName;
		}
		return val;
	}

	private TagSyncConfig() {
		super(false);
		init() ;
	}

	private void init() {
		readConfigFile(DEFAULT_CONFIG_FILE);
		readConfigFile(CONFIG_FILE);
	}

	private void readConfigFile(String fileName) {

		if (StringUtils.isNotBlank(fileName)) {
			String fName = getResourceFileName(fileName);
			if (StringUtils.isBlank(fName)) {
				LOG.warn("Cannot find configuration file " + fileName + " in the classpath");
			} else {
				LOG.info("Loading configuration from " + fName);
				addResource(fileName);
			}
		} else {
			LOG.error("Configuration fileName is null");
		}
	}

}
