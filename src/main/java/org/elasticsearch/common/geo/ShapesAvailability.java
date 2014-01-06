begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|Classes
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|ShapesAvailability
specifier|public
class|class
name|ShapesAvailability
block|{
DECL|field|SPATIAL4J_AVAILABLE
specifier|public
specifier|static
specifier|final
name|boolean
name|SPATIAL4J_AVAILABLE
decl_stmt|;
DECL|field|JTS_AVAILABLE
specifier|public
specifier|static
specifier|final
name|boolean
name|JTS_AVAILABLE
decl_stmt|;
static|static
block|{
name|boolean
name|xSPATIAL4J_AVAILABLE
decl_stmt|;
try|try
block|{
name|Classes
operator|.
name|getDefaultClassLoader
argument_list|()
operator|.
name|loadClass
argument_list|(
literal|"com.spatial4j.core.shape.impl.PointImpl"
argument_list|)
expr_stmt|;
name|xSPATIAL4J_AVAILABLE
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|xSPATIAL4J_AVAILABLE
operator|=
literal|false
expr_stmt|;
block|}
name|SPATIAL4J_AVAILABLE
operator|=
name|xSPATIAL4J_AVAILABLE
expr_stmt|;
name|boolean
name|xJTS_AVAILABLE
decl_stmt|;
try|try
block|{
name|Classes
operator|.
name|getDefaultClassLoader
argument_list|()
operator|.
name|loadClass
argument_list|(
literal|"com.vividsolutions.jts.geom.GeometryFactory"
argument_list|)
expr_stmt|;
name|xJTS_AVAILABLE
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|xJTS_AVAILABLE
operator|=
literal|false
expr_stmt|;
block|}
name|JTS_AVAILABLE
operator|=
name|xJTS_AVAILABLE
expr_stmt|;
block|}
DECL|method|ShapesAvailability
specifier|private
name|ShapesAvailability
parameter_list|()
block|{      }
block|}
end_class

end_unit

