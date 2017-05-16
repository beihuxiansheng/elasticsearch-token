begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.block
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|block
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_enum
DECL|enum|ClusterBlockLevel
specifier|public
enum|enum
name|ClusterBlockLevel
block|{
DECL|enum constant|READ
name|READ
block|,
DECL|enum constant|WRITE
name|WRITE
block|,
DECL|enum constant|METADATA_READ
name|METADATA_READ
block|,
DECL|enum constant|METADATA_WRITE
name|METADATA_WRITE
block|;
DECL|field|ALL
specifier|public
specifier|static
specifier|final
name|EnumSet
argument_list|<
name|ClusterBlockLevel
argument_list|>
name|ALL
init|=
name|EnumSet
operator|.
name|allOf
argument_list|(
name|ClusterBlockLevel
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|READ_WRITE
specifier|public
specifier|static
specifier|final
name|EnumSet
argument_list|<
name|ClusterBlockLevel
argument_list|>
name|READ_WRITE
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|READ
argument_list|,
name|WRITE
argument_list|)
decl_stmt|;
block|}
end_enum

end_unit

