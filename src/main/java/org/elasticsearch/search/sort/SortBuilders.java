begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.sort
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|sort
package|;
end_package

begin_comment
comment|/**  * A set of static factory methods for {@link SortBuilder}s.  *  *  */
end_comment

begin_class
DECL|class|SortBuilders
specifier|public
class|class
name|SortBuilders
block|{
comment|/**      * Constructs a new score sort.      */
DECL|method|scoreSort
specifier|public
specifier|static
name|ScoreSortBuilder
name|scoreSort
parameter_list|()
block|{
return|return
operator|new
name|ScoreSortBuilder
argument_list|()
return|;
block|}
comment|/**      * Constructs a new field based sort.      *      * @param field The field name.      */
DECL|method|fieldSort
specifier|public
specifier|static
name|FieldSortBuilder
name|fieldSort
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
operator|new
name|FieldSortBuilder
argument_list|(
name|field
argument_list|)
return|;
block|}
comment|/**      * Constructs a new script based sort.      *      * @param script The script to use.      * @param type   The type, can either be "string" or "number".      */
DECL|method|scriptSort
specifier|public
specifier|static
name|ScriptSortBuilder
name|scriptSort
parameter_list|(
name|String
name|script
parameter_list|,
name|String
name|type
parameter_list|)
block|{
return|return
operator|new
name|ScriptSortBuilder
argument_list|(
name|script
argument_list|,
name|type
argument_list|)
return|;
block|}
comment|/**      * A geo distance based sort.      *      * @param fieldName The geo point like field name.      */
DECL|method|geoDistanceSort
specifier|public
specifier|static
name|GeoDistanceSortBuilder
name|geoDistanceSort
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
operator|new
name|GeoDistanceSortBuilder
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

