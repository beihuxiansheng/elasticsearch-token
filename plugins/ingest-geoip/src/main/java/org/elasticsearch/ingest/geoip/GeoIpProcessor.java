begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.ingest.geoip
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|geoip
package|;
end_package

begin_import
import|import
name|com
operator|.
name|maxmind
operator|.
name|geoip2
operator|.
name|DatabaseReader
import|;
end_import

begin_import
import|import
name|com
operator|.
name|maxmind
operator|.
name|geoip2
operator|.
name|exception
operator|.
name|AddressNotFoundException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|maxmind
operator|.
name|geoip2
operator|.
name|model
operator|.
name|CityResponse
import|;
end_import

begin_import
import|import
name|com
operator|.
name|maxmind
operator|.
name|geoip2
operator|.
name|model
operator|.
name|CountryResponse
import|;
end_import

begin_import
import|import
name|com
operator|.
name|maxmind
operator|.
name|geoip2
operator|.
name|record
operator|.
name|City
import|;
end_import

begin_import
import|import
name|com
operator|.
name|maxmind
operator|.
name|geoip2
operator|.
name|record
operator|.
name|Continent
import|;
end_import

begin_import
import|import
name|com
operator|.
name|maxmind
operator|.
name|geoip2
operator|.
name|record
operator|.
name|Country
import|;
end_import

begin_import
import|import
name|com
operator|.
name|maxmind
operator|.
name|geoip2
operator|.
name|record
operator|.
name|Location
import|;
end_import

begin_import
import|import
name|com
operator|.
name|maxmind
operator|.
name|geoip2
operator|.
name|record
operator|.
name|Subdivision
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchParseException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|SpecialPermission
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|network
operator|.
name|InetAddresses
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|network
operator|.
name|NetworkAddress
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|core
operator|.
name|AbstractProcessor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|core
operator|.
name|AbstractProcessorFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|core
operator|.
name|IngestDocument
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|AccessController
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|core
operator|.
name|ConfigurationUtils
operator|.
name|newConfigurationException
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|core
operator|.
name|ConfigurationUtils
operator|.
name|readOptionalList
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|core
operator|.
name|ConfigurationUtils
operator|.
name|readStringProperty
import|;
end_import

begin_class
DECL|class|GeoIpProcessor
specifier|public
specifier|final
class|class
name|GeoIpProcessor
extends|extends
name|AbstractProcessor
block|{
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|String
name|TYPE
init|=
literal|"geoip"
decl_stmt|;
DECL|field|sourceField
specifier|private
specifier|final
name|String
name|sourceField
decl_stmt|;
DECL|field|targetField
specifier|private
specifier|final
name|String
name|targetField
decl_stmt|;
DECL|field|dbReader
specifier|private
specifier|final
name|DatabaseReader
name|dbReader
decl_stmt|;
DECL|field|fields
specifier|private
specifier|final
name|Set
argument_list|<
name|Field
argument_list|>
name|fields
decl_stmt|;
DECL|method|GeoIpProcessor
name|GeoIpProcessor
parameter_list|(
name|String
name|tag
parameter_list|,
name|String
name|sourceField
parameter_list|,
name|DatabaseReader
name|dbReader
parameter_list|,
name|String
name|targetField
parameter_list|,
name|Set
argument_list|<
name|Field
argument_list|>
name|fields
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|tag
argument_list|)
expr_stmt|;
name|this
operator|.
name|sourceField
operator|=
name|sourceField
expr_stmt|;
name|this
operator|.
name|targetField
operator|=
name|targetField
expr_stmt|;
name|this
operator|.
name|dbReader
operator|=
name|dbReader
expr_stmt|;
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|(
name|IngestDocument
name|ingestDocument
parameter_list|)
block|{
name|String
name|ip
init|=
name|ingestDocument
operator|.
name|getFieldValue
argument_list|(
name|sourceField
argument_list|,
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|InetAddress
name|ipAddress
init|=
name|InetAddresses
operator|.
name|forString
argument_list|(
name|ip
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|geoData
decl_stmt|;
switch|switch
condition|(
name|dbReader
operator|.
name|getMetadata
argument_list|()
operator|.
name|getDatabaseType
argument_list|()
condition|)
block|{
case|case
literal|"GeoLite2-City"
case|:
try|try
block|{
name|geoData
operator|=
name|retrieveCityGeoData
argument_list|(
name|ipAddress
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AddressNotFoundRuntimeException
name|e
parameter_list|)
block|{
name|geoData
operator|=
name|Collections
operator|.
name|emptyMap
argument_list|()
expr_stmt|;
block|}
break|break;
case|case
literal|"GeoLite2-Country"
case|:
try|try
block|{
name|geoData
operator|=
name|retrieveCountryGeoData
argument_list|(
name|ipAddress
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AddressNotFoundRuntimeException
name|e
parameter_list|)
block|{
name|geoData
operator|=
name|Collections
operator|.
name|emptyMap
argument_list|()
expr_stmt|;
block|}
break|break;
default|default:
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Unsupported database type ["
operator|+
name|dbReader
operator|.
name|getMetadata
argument_list|()
operator|.
name|getDatabaseType
argument_list|()
operator|+
literal|"]"
argument_list|,
operator|new
name|IllegalStateException
argument_list|()
argument_list|)
throw|;
block|}
name|ingestDocument
operator|.
name|setFieldValue
argument_list|(
name|targetField
argument_list|,
name|geoData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getType
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|TYPE
return|;
block|}
DECL|method|getSourceField
name|String
name|getSourceField
parameter_list|()
block|{
return|return
name|sourceField
return|;
block|}
DECL|method|getTargetField
name|String
name|getTargetField
parameter_list|()
block|{
return|return
name|targetField
return|;
block|}
DECL|method|getDbReader
name|DatabaseReader
name|getDbReader
parameter_list|()
block|{
return|return
name|dbReader
return|;
block|}
DECL|method|getFields
name|Set
argument_list|<
name|Field
argument_list|>
name|getFields
parameter_list|()
block|{
return|return
name|fields
return|;
block|}
DECL|method|retrieveCityGeoData
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|retrieveCityGeoData
parameter_list|(
name|InetAddress
name|ipAddress
parameter_list|)
block|{
name|SecurityManager
name|sm
init|=
name|System
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
if|if
condition|(
name|sm
operator|!=
literal|null
condition|)
block|{
name|sm
operator|.
name|checkPermission
argument_list|(
operator|new
name|SpecialPermission
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|CityResponse
name|response
init|=
name|AccessController
operator|.
name|doPrivileged
argument_list|(
call|(
name|PrivilegedAction
argument_list|<
name|CityResponse
argument_list|>
call|)
argument_list|()
operator|->
block|{
try|try
block|{
return|return
name|dbReader
operator|.
name|city
argument_list|(
name|ipAddress
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|AddressNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AddressNotFoundRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
decl_stmt|;
name|Country
name|country
init|=
name|response
operator|.
name|getCountry
argument_list|()
decl_stmt|;
name|City
name|city
init|=
name|response
operator|.
name|getCity
argument_list|()
decl_stmt|;
name|Location
name|location
init|=
name|response
operator|.
name|getLocation
argument_list|()
decl_stmt|;
name|Continent
name|continent
init|=
name|response
operator|.
name|getContinent
argument_list|()
decl_stmt|;
name|Subdivision
name|subdivision
init|=
name|response
operator|.
name|getMostSpecificSubdivision
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|geoData
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Field
name|field
range|:
name|fields
control|)
block|{
switch|switch
condition|(
name|field
condition|)
block|{
case|case
name|IP
case|:
name|geoData
operator|.
name|put
argument_list|(
literal|"ip"
argument_list|,
name|NetworkAddress
operator|.
name|formatAddress
argument_list|(
name|ipAddress
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|COUNTRY_ISO_CODE
case|:
name|geoData
operator|.
name|put
argument_list|(
literal|"country_iso_code"
argument_list|,
name|country
operator|.
name|getIsoCode
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|COUNTRY_NAME
case|:
name|geoData
operator|.
name|put
argument_list|(
literal|"country_name"
argument_list|,
name|country
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|CONTINENT_NAME
case|:
name|geoData
operator|.
name|put
argument_list|(
literal|"continent_name"
argument_list|,
name|continent
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|REGION_NAME
case|:
name|geoData
operator|.
name|put
argument_list|(
literal|"region_name"
argument_list|,
name|subdivision
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|CITY_NAME
case|:
name|geoData
operator|.
name|put
argument_list|(
literal|"city_name"
argument_list|,
name|city
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|TIMEZONE
case|:
name|geoData
operator|.
name|put
argument_list|(
literal|"timezone"
argument_list|,
name|location
operator|.
name|getTimeZone
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|LOCATION
case|:
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|locationObject
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|locationObject
operator|.
name|put
argument_list|(
literal|"lat"
argument_list|,
name|location
operator|.
name|getLatitude
argument_list|()
argument_list|)
expr_stmt|;
name|locationObject
operator|.
name|put
argument_list|(
literal|"lon"
argument_list|,
name|location
operator|.
name|getLongitude
argument_list|()
argument_list|)
expr_stmt|;
name|geoData
operator|.
name|put
argument_list|(
literal|"location"
argument_list|,
name|locationObject
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
return|return
name|geoData
return|;
block|}
DECL|method|retrieveCountryGeoData
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|retrieveCountryGeoData
parameter_list|(
name|InetAddress
name|ipAddress
parameter_list|)
block|{
name|SecurityManager
name|sm
init|=
name|System
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
if|if
condition|(
name|sm
operator|!=
literal|null
condition|)
block|{
name|sm
operator|.
name|checkPermission
argument_list|(
operator|new
name|SpecialPermission
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|CountryResponse
name|response
init|=
name|AccessController
operator|.
name|doPrivileged
argument_list|(
call|(
name|PrivilegedAction
argument_list|<
name|CountryResponse
argument_list|>
call|)
argument_list|()
operator|->
block|{
try|try
block|{
return|return
name|dbReader
operator|.
name|country
argument_list|(
name|ipAddress
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|AddressNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AddressNotFoundRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
decl_stmt|;
name|Country
name|country
init|=
name|response
operator|.
name|getCountry
argument_list|()
decl_stmt|;
name|Continent
name|continent
init|=
name|response
operator|.
name|getContinent
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|geoData
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Field
name|field
range|:
name|fields
control|)
block|{
switch|switch
condition|(
name|field
condition|)
block|{
case|case
name|IP
case|:
name|geoData
operator|.
name|put
argument_list|(
literal|"ip"
argument_list|,
name|NetworkAddress
operator|.
name|formatAddress
argument_list|(
name|ipAddress
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|COUNTRY_ISO_CODE
case|:
name|geoData
operator|.
name|put
argument_list|(
literal|"country_iso_code"
argument_list|,
name|country
operator|.
name|getIsoCode
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|COUNTRY_NAME
case|:
name|geoData
operator|.
name|put
argument_list|(
literal|"country_name"
argument_list|,
name|country
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|CONTINENT_NAME
case|:
name|geoData
operator|.
name|put
argument_list|(
literal|"continent_name"
argument_list|,
name|continent
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
return|return
name|geoData
return|;
block|}
DECL|class|Factory
specifier|public
specifier|static
specifier|final
class|class
name|Factory
extends|extends
name|AbstractProcessorFactory
argument_list|<
name|GeoIpProcessor
argument_list|>
implements|implements
name|Closeable
block|{
DECL|field|DEFAULT_FIELDS
specifier|static
specifier|final
name|Set
argument_list|<
name|Field
argument_list|>
name|DEFAULT_FIELDS
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|Field
operator|.
name|CONTINENT_NAME
argument_list|,
name|Field
operator|.
name|COUNTRY_ISO_CODE
argument_list|,
name|Field
operator|.
name|REGION_NAME
argument_list|,
name|Field
operator|.
name|CITY_NAME
argument_list|,
name|Field
operator|.
name|LOCATION
argument_list|)
decl_stmt|;
DECL|field|databaseReaders
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|DatabaseReader
argument_list|>
name|databaseReaders
decl_stmt|;
DECL|method|Factory
specifier|public
name|Factory
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|DatabaseReader
argument_list|>
name|databaseReaders
parameter_list|)
block|{
name|this
operator|.
name|databaseReaders
operator|=
name|databaseReaders
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doCreate
specifier|public
name|GeoIpProcessor
name|doCreate
parameter_list|(
name|String
name|processorTag
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|ipField
init|=
name|readStringProperty
argument_list|(
name|TYPE
argument_list|,
name|processorTag
argument_list|,
name|config
argument_list|,
literal|"source_field"
argument_list|)
decl_stmt|;
name|String
name|targetField
init|=
name|readStringProperty
argument_list|(
name|TYPE
argument_list|,
name|processorTag
argument_list|,
name|config
argument_list|,
literal|"target_field"
argument_list|,
literal|"geoip"
argument_list|)
decl_stmt|;
name|String
name|databaseFile
init|=
name|readStringProperty
argument_list|(
name|TYPE
argument_list|,
name|processorTag
argument_list|,
name|config
argument_list|,
literal|"database_file"
argument_list|,
literal|"GeoLite2-City.mmdb"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
init|=
name|readOptionalList
argument_list|(
name|TYPE
argument_list|,
name|processorTag
argument_list|,
name|config
argument_list|,
literal|"fields"
argument_list|)
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|Field
argument_list|>
name|fields
decl_stmt|;
if|if
condition|(
name|fieldNames
operator|!=
literal|null
condition|)
block|{
name|fields
operator|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|Field
operator|.
name|class
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|fieldName
range|:
name|fieldNames
control|)
block|{
try|try
block|{
name|fields
operator|.
name|add
argument_list|(
name|Field
operator|.
name|parse
argument_list|(
name|fieldName
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|newConfigurationException
argument_list|(
name|TYPE
argument_list|,
name|processorTag
argument_list|,
literal|"fields"
argument_list|,
literal|"illegal field option ["
operator|+
name|fieldName
operator|+
literal|"]. valid values are ["
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|Field
operator|.
name|values
argument_list|()
argument_list|)
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
else|else
block|{
name|fields
operator|=
name|DEFAULT_FIELDS
expr_stmt|;
block|}
name|DatabaseReader
name|databaseReader
init|=
name|databaseReaders
operator|.
name|get
argument_list|(
name|databaseFile
argument_list|)
decl_stmt|;
if|if
condition|(
name|databaseReader
operator|==
literal|null
condition|)
block|{
throw|throw
name|newConfigurationException
argument_list|(
name|TYPE
argument_list|,
name|processorTag
argument_list|,
literal|"database_file"
argument_list|,
literal|"database file ["
operator|+
name|databaseFile
operator|+
literal|"] doesn't exist"
argument_list|)
throw|;
block|}
return|return
operator|new
name|GeoIpProcessor
argument_list|(
name|processorTag
argument_list|,
name|ipField
argument_list|,
name|databaseReader
argument_list|,
name|targetField
argument_list|,
name|fields
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|databaseReaders
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Geoip2's AddressNotFoundException is checked and due to the fact that we need run their code
comment|// inside a PrivilegedAction code block, we are forced to catch any checked exception and rethrow
comment|// it with an unchecked exception.
DECL|class|AddressNotFoundRuntimeException
specifier|private
specifier|final
specifier|static
class|class
name|AddressNotFoundRuntimeException
extends|extends
name|RuntimeException
block|{
DECL|method|AddressNotFoundRuntimeException
specifier|public
name|AddressNotFoundRuntimeException
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
DECL|enum|Field
specifier|public
enum|enum
name|Field
block|{
DECL|enum constant|IP
name|IP
block|,
DECL|enum constant|COUNTRY_ISO_CODE
name|COUNTRY_ISO_CODE
block|,
DECL|enum constant|COUNTRY_NAME
name|COUNTRY_NAME
block|,
DECL|enum constant|CONTINENT_NAME
name|CONTINENT_NAME
block|,
DECL|enum constant|REGION_NAME
name|REGION_NAME
block|,
DECL|enum constant|CITY_NAME
name|CITY_NAME
block|,
DECL|enum constant|TIMEZONE
name|TIMEZONE
block|,
DECL|enum constant|LATITUDE
name|LATITUDE
block|,
DECL|enum constant|LONGITUDE
name|LONGITUDE
block|,
DECL|enum constant|LOCATION
name|LOCATION
block|;
DECL|method|parse
specifier|public
specifier|static
name|Field
name|parse
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|valueOf
argument_list|(
name|value
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit
