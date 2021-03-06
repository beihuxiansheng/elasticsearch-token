begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing.allocation
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
operator|.
name|allocation
package|;
end_package

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
name|common
operator|.
name|settings
operator|.
name|ClusterSettings
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
name|settings
operator|.
name|Setting
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
name|settings
operator|.
name|Settings
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
name|unit
operator|.
name|ByteSizeValue
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
name|unit
operator|.
name|RatioValue
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
name|unit
operator|.
name|TimeValue
import|;
end_import

begin_comment
comment|/**  * A container to keep settings for disk thresholds up to date with cluster setting changes.  */
end_comment

begin_class
DECL|class|DiskThresholdSettings
specifier|public
class|class
name|DiskThresholdSettings
block|{
DECL|field|CLUSTER_ROUTING_ALLOCATION_DISK_THRESHOLD_ENABLED_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Boolean
argument_list|>
name|CLUSTER_ROUTING_ALLOCATION_DISK_THRESHOLD_ENABLED_SETTING
init|=
name|Setting
operator|.
name|boolSetting
argument_list|(
literal|"cluster.routing.allocation.disk.threshold_enabled"
argument_list|,
literal|true
argument_list|,
name|Setting
operator|.
name|Property
operator|.
name|Dynamic
argument_list|,
name|Setting
operator|.
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
DECL|field|CLUSTER_ROUTING_ALLOCATION_LOW_DISK_WATERMARK_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|String
argument_list|>
name|CLUSTER_ROUTING_ALLOCATION_LOW_DISK_WATERMARK_SETTING
init|=
operator|new
name|Setting
argument_list|<>
argument_list|(
literal|"cluster.routing.allocation.disk.watermark.low"
argument_list|,
literal|"85%"
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|validWatermarkSetting
argument_list|(
name|s
argument_list|,
literal|"cluster.routing.allocation.disk.watermark.low"
argument_list|)
argument_list|,
name|Setting
operator|.
name|Property
operator|.
name|Dynamic
argument_list|,
name|Setting
operator|.
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
DECL|field|CLUSTER_ROUTING_ALLOCATION_HIGH_DISK_WATERMARK_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|String
argument_list|>
name|CLUSTER_ROUTING_ALLOCATION_HIGH_DISK_WATERMARK_SETTING
init|=
operator|new
name|Setting
argument_list|<>
argument_list|(
literal|"cluster.routing.allocation.disk.watermark.high"
argument_list|,
literal|"90%"
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|validWatermarkSetting
argument_list|(
name|s
argument_list|,
literal|"cluster.routing.allocation.disk.watermark.high"
argument_list|)
argument_list|,
name|Setting
operator|.
name|Property
operator|.
name|Dynamic
argument_list|,
name|Setting
operator|.
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
DECL|field|CLUSTER_ROUTING_ALLOCATION_INCLUDE_RELOCATIONS_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Boolean
argument_list|>
name|CLUSTER_ROUTING_ALLOCATION_INCLUDE_RELOCATIONS_SETTING
init|=
name|Setting
operator|.
name|boolSetting
argument_list|(
literal|"cluster.routing.allocation.disk.include_relocations"
argument_list|,
literal|true
argument_list|,
name|Setting
operator|.
name|Property
operator|.
name|Dynamic
argument_list|,
name|Setting
operator|.
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
empty_stmt|;
DECL|field|CLUSTER_ROUTING_ALLOCATION_REROUTE_INTERVAL_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|TimeValue
argument_list|>
name|CLUSTER_ROUTING_ALLOCATION_REROUTE_INTERVAL_SETTING
init|=
name|Setting
operator|.
name|positiveTimeSetting
argument_list|(
literal|"cluster.routing.allocation.disk.reroute_interval"
argument_list|,
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|60
argument_list|)
argument_list|,
name|Setting
operator|.
name|Property
operator|.
name|Dynamic
argument_list|,
name|Setting
operator|.
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
DECL|field|lowWatermarkRaw
specifier|private
specifier|volatile
name|String
name|lowWatermarkRaw
decl_stmt|;
DECL|field|highWatermarkRaw
specifier|private
specifier|volatile
name|String
name|highWatermarkRaw
decl_stmt|;
DECL|field|freeDiskThresholdLow
specifier|private
specifier|volatile
name|Double
name|freeDiskThresholdLow
decl_stmt|;
DECL|field|freeDiskThresholdHigh
specifier|private
specifier|volatile
name|Double
name|freeDiskThresholdHigh
decl_stmt|;
DECL|field|freeBytesThresholdLow
specifier|private
specifier|volatile
name|ByteSizeValue
name|freeBytesThresholdLow
decl_stmt|;
DECL|field|freeBytesThresholdHigh
specifier|private
specifier|volatile
name|ByteSizeValue
name|freeBytesThresholdHigh
decl_stmt|;
DECL|field|includeRelocations
specifier|private
specifier|volatile
name|boolean
name|includeRelocations
decl_stmt|;
DECL|field|enabled
specifier|private
specifier|volatile
name|boolean
name|enabled
decl_stmt|;
DECL|field|rerouteInterval
specifier|private
specifier|volatile
name|TimeValue
name|rerouteInterval
decl_stmt|;
DECL|method|DiskThresholdSettings
specifier|public
name|DiskThresholdSettings
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ClusterSettings
name|clusterSettings
parameter_list|)
block|{
specifier|final
name|String
name|lowWatermark
init|=
name|CLUSTER_ROUTING_ALLOCATION_LOW_DISK_WATERMARK_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
decl_stmt|;
specifier|final
name|String
name|highWatermark
init|=
name|CLUSTER_ROUTING_ALLOCATION_HIGH_DISK_WATERMARK_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|setHighWatermark
argument_list|(
name|highWatermark
argument_list|)
expr_stmt|;
name|setLowWatermark
argument_list|(
name|lowWatermark
argument_list|)
expr_stmt|;
name|this
operator|.
name|includeRelocations
operator|=
name|CLUSTER_ROUTING_ALLOCATION_INCLUDE_RELOCATIONS_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|rerouteInterval
operator|=
name|CLUSTER_ROUTING_ALLOCATION_REROUTE_INTERVAL_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|enabled
operator|=
name|CLUSTER_ROUTING_ALLOCATION_DISK_THRESHOLD_ENABLED_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|clusterSettings
operator|.
name|addSettingsUpdateConsumer
argument_list|(
name|CLUSTER_ROUTING_ALLOCATION_LOW_DISK_WATERMARK_SETTING
argument_list|,
name|this
operator|::
name|setLowWatermark
argument_list|)
expr_stmt|;
name|clusterSettings
operator|.
name|addSettingsUpdateConsumer
argument_list|(
name|CLUSTER_ROUTING_ALLOCATION_HIGH_DISK_WATERMARK_SETTING
argument_list|,
name|this
operator|::
name|setHighWatermark
argument_list|)
expr_stmt|;
name|clusterSettings
operator|.
name|addSettingsUpdateConsumer
argument_list|(
name|CLUSTER_ROUTING_ALLOCATION_INCLUDE_RELOCATIONS_SETTING
argument_list|,
name|this
operator|::
name|setIncludeRelocations
argument_list|)
expr_stmt|;
name|clusterSettings
operator|.
name|addSettingsUpdateConsumer
argument_list|(
name|CLUSTER_ROUTING_ALLOCATION_REROUTE_INTERVAL_SETTING
argument_list|,
name|this
operator|::
name|setRerouteInterval
argument_list|)
expr_stmt|;
name|clusterSettings
operator|.
name|addSettingsUpdateConsumer
argument_list|(
name|CLUSTER_ROUTING_ALLOCATION_DISK_THRESHOLD_ENABLED_SETTING
argument_list|,
name|this
operator|::
name|setEnabled
argument_list|)
expr_stmt|;
block|}
DECL|method|setIncludeRelocations
specifier|private
name|void
name|setIncludeRelocations
parameter_list|(
name|boolean
name|includeRelocations
parameter_list|)
block|{
name|this
operator|.
name|includeRelocations
operator|=
name|includeRelocations
expr_stmt|;
block|}
DECL|method|setRerouteInterval
specifier|private
name|void
name|setRerouteInterval
parameter_list|(
name|TimeValue
name|rerouteInterval
parameter_list|)
block|{
name|this
operator|.
name|rerouteInterval
operator|=
name|rerouteInterval
expr_stmt|;
block|}
DECL|method|setEnabled
specifier|private
name|void
name|setEnabled
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
name|this
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
block|}
DECL|method|setLowWatermark
specifier|private
name|void
name|setLowWatermark
parameter_list|(
name|String
name|lowWatermark
parameter_list|)
block|{
comment|// Watermark is expressed in terms of used data, but we need "free" data watermark
name|this
operator|.
name|lowWatermarkRaw
operator|=
name|lowWatermark
expr_stmt|;
name|this
operator|.
name|freeDiskThresholdLow
operator|=
literal|100.0
operator|-
name|thresholdPercentageFromWatermark
argument_list|(
name|lowWatermark
argument_list|)
expr_stmt|;
name|this
operator|.
name|freeBytesThresholdLow
operator|=
name|thresholdBytesFromWatermark
argument_list|(
name|lowWatermark
argument_list|,
name|CLUSTER_ROUTING_ALLOCATION_LOW_DISK_WATERMARK_SETTING
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setHighWatermark
specifier|private
name|void
name|setHighWatermark
parameter_list|(
name|String
name|highWatermark
parameter_list|)
block|{
comment|// Watermark is expressed in terms of used data, but we need "free" data watermark
name|this
operator|.
name|highWatermarkRaw
operator|=
name|highWatermark
expr_stmt|;
name|this
operator|.
name|freeDiskThresholdHigh
operator|=
literal|100.0
operator|-
name|thresholdPercentageFromWatermark
argument_list|(
name|highWatermark
argument_list|)
expr_stmt|;
name|this
operator|.
name|freeBytesThresholdHigh
operator|=
name|thresholdBytesFromWatermark
argument_list|(
name|highWatermark
argument_list|,
name|CLUSTER_ROUTING_ALLOCATION_LOW_DISK_WATERMARK_SETTING
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Gets the raw (uninterpreted) low watermark value as found in the settings.      */
DECL|method|getLowWatermarkRaw
specifier|public
name|String
name|getLowWatermarkRaw
parameter_list|()
block|{
return|return
name|lowWatermarkRaw
return|;
block|}
comment|/**      * Gets the raw (uninterpreted) high watermark value as found in the settings.      */
DECL|method|getHighWatermarkRaw
specifier|public
name|String
name|getHighWatermarkRaw
parameter_list|()
block|{
return|return
name|highWatermarkRaw
return|;
block|}
DECL|method|getFreeDiskThresholdLow
specifier|public
name|Double
name|getFreeDiskThresholdLow
parameter_list|()
block|{
return|return
name|freeDiskThresholdLow
return|;
block|}
DECL|method|getFreeDiskThresholdHigh
specifier|public
name|Double
name|getFreeDiskThresholdHigh
parameter_list|()
block|{
return|return
name|freeDiskThresholdHigh
return|;
block|}
DECL|method|getFreeBytesThresholdLow
specifier|public
name|ByteSizeValue
name|getFreeBytesThresholdLow
parameter_list|()
block|{
return|return
name|freeBytesThresholdLow
return|;
block|}
DECL|method|getFreeBytesThresholdHigh
specifier|public
name|ByteSizeValue
name|getFreeBytesThresholdHigh
parameter_list|()
block|{
return|return
name|freeBytesThresholdHigh
return|;
block|}
DECL|method|includeRelocations
specifier|public
name|boolean
name|includeRelocations
parameter_list|()
block|{
return|return
name|includeRelocations
return|;
block|}
DECL|method|isEnabled
specifier|public
name|boolean
name|isEnabled
parameter_list|()
block|{
return|return
name|enabled
return|;
block|}
DECL|method|getRerouteInterval
specifier|public
name|TimeValue
name|getRerouteInterval
parameter_list|()
block|{
return|return
name|rerouteInterval
return|;
block|}
comment|/**      * Attempts to parse the watermark into a percentage, returning 100.0% if      * it cannot be parsed.      */
DECL|method|thresholdPercentageFromWatermark
specifier|private
name|double
name|thresholdPercentageFromWatermark
parameter_list|(
name|String
name|watermark
parameter_list|)
block|{
try|try
block|{
return|return
name|RatioValue
operator|.
name|parseRatioValue
argument_list|(
name|watermark
argument_list|)
operator|.
name|getAsPercent
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ElasticsearchParseException
name|ex
parameter_list|)
block|{
comment|// NOTE: this is not end-user leniency, since up above we check that it's a valid byte or percentage, and then store the two
comment|// cases separately
return|return
literal|100.0
return|;
block|}
block|}
comment|/**      * Attempts to parse the watermark into a {@link ByteSizeValue}, returning      * a ByteSizeValue of 0 bytes if the value cannot be parsed.      */
DECL|method|thresholdBytesFromWatermark
specifier|private
name|ByteSizeValue
name|thresholdBytesFromWatermark
parameter_list|(
name|String
name|watermark
parameter_list|,
name|String
name|settingName
parameter_list|)
block|{
try|try
block|{
return|return
name|ByteSizeValue
operator|.
name|parseBytesSizeValue
argument_list|(
name|watermark
argument_list|,
name|settingName
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ElasticsearchParseException
name|ex
parameter_list|)
block|{
comment|// NOTE: this is not end-user leniency, since up above we check that it's a valid byte or percentage, and then store the two
comment|// cases separately
return|return
name|ByteSizeValue
operator|.
name|parseBytesSizeValue
argument_list|(
literal|"0b"
argument_list|,
name|settingName
argument_list|)
return|;
block|}
block|}
comment|/**      * Checks if a watermark string is a valid percentage or byte size value,      * @return the watermark value given      */
DECL|method|validWatermarkSetting
specifier|private
specifier|static
name|String
name|validWatermarkSetting
parameter_list|(
name|String
name|watermark
parameter_list|,
name|String
name|settingName
parameter_list|)
block|{
try|try
block|{
name|RatioValue
operator|.
name|parseRatioValue
argument_list|(
name|watermark
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ElasticsearchParseException
name|e
parameter_list|)
block|{
try|try
block|{
name|ByteSizeValue
operator|.
name|parseBytesSizeValue
argument_list|(
name|watermark
argument_list|,
name|settingName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ElasticsearchParseException
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|addSuppressed
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
block|}
return|return
name|watermark
return|;
block|}
block|}
end_class

end_unit

