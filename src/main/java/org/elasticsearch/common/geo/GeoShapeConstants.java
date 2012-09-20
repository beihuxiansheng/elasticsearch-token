begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.geo
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|geo
package|;
end_package

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|context
operator|.
name|jts
operator|.
name|JtsSpatialContext
import|;
end_import

begin_comment
comment|/**  * Common constants through the GeoShape codebase  */
end_comment

begin_interface
DECL|interface|GeoShapeConstants
specifier|public
interface|interface
name|GeoShapeConstants
block|{
DECL|field|SPATIAL_CONTEXT
specifier|public
specifier|static
specifier|final
name|JtsSpatialContext
name|SPATIAL_CONTEXT
init|=
operator|new
name|JtsSpatialContext
argument_list|(
literal|true
argument_list|)
decl_stmt|;
block|}
end_interface

end_unit

