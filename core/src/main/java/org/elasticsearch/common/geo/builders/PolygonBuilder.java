begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.geo.builders
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|geo
operator|.
name|builders
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|com
operator|.
name|vividsolutions
operator|.
name|jts
operator|.
name|geom
operator|.
name|Coordinate
import|;
end_import

begin_class
DECL|class|PolygonBuilder
specifier|public
class|class
name|PolygonBuilder
extends|extends
name|BasePolygonBuilder
argument_list|<
name|PolygonBuilder
argument_list|>
block|{
DECL|method|PolygonBuilder
specifier|public
name|PolygonBuilder
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|Coordinate
argument_list|>
argument_list|()
argument_list|,
name|Orientation
operator|.
name|RIGHT
argument_list|)
expr_stmt|;
block|}
DECL|method|PolygonBuilder
specifier|public
name|PolygonBuilder
parameter_list|(
name|Orientation
name|orientation
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|Coordinate
argument_list|>
argument_list|()
argument_list|,
name|orientation
argument_list|)
expr_stmt|;
block|}
DECL|method|PolygonBuilder
specifier|protected
name|PolygonBuilder
parameter_list|(
name|ArrayList
argument_list|<
name|Coordinate
argument_list|>
name|points
parameter_list|,
name|Orientation
name|orientation
parameter_list|)
block|{
name|super
argument_list|(
name|orientation
argument_list|)
expr_stmt|;
name|this
operator|.
name|shell
operator|=
operator|new
name|LineStringBuilder
argument_list|(
name|points
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|PolygonBuilder
name|close
parameter_list|()
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

