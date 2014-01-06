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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|FilterBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalArgumentException
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

begin_comment
comment|/**  * A sort builder to sort based on a document field.  */
end_comment

begin_class
DECL|class|FieldSortBuilder
specifier|public
class|class
name|FieldSortBuilder
extends|extends
name|SortBuilder
block|{
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|order
specifier|private
name|SortOrder
name|order
decl_stmt|;
DECL|field|missing
specifier|private
name|Object
name|missing
decl_stmt|;
DECL|field|ignoreUnampped
specifier|private
name|Boolean
name|ignoreUnampped
decl_stmt|;
DECL|field|sortMode
specifier|private
name|String
name|sortMode
decl_stmt|;
DECL|field|nestedFilter
specifier|private
name|FilterBuilder
name|nestedFilter
decl_stmt|;
DECL|field|nestedPath
specifier|private
name|String
name|nestedPath
decl_stmt|;
comment|/**      * Constructs a new sort based on a document field.      *      * @param fieldName The field name.      */
DECL|method|FieldSortBuilder
specifier|public
name|FieldSortBuilder
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
if|if
condition|(
name|fieldName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"fieldName must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
block|}
comment|/**      * The order of sorting. Defaults to {@link SortOrder#ASC}.      */
annotation|@
name|Override
DECL|method|order
specifier|public
name|FieldSortBuilder
name|order
parameter_list|(
name|SortOrder
name|order
parameter_list|)
block|{
name|this
operator|.
name|order
operator|=
name|order
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the value when a field is missing in a doc. Can also be set to<tt>_last</tt> or      *<tt>_first</tt> to sort missing last or first respectively.      */
annotation|@
name|Override
DECL|method|missing
specifier|public
name|FieldSortBuilder
name|missing
parameter_list|(
name|Object
name|missing
parameter_list|)
block|{
name|this
operator|.
name|missing
operator|=
name|missing
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets if the field does not exists in the index, it should be ignored and not sorted by or not. Defaults      * to<tt>false</tt> (not ignoring).      */
DECL|method|ignoreUnmapped
specifier|public
name|FieldSortBuilder
name|ignoreUnmapped
parameter_list|(
name|boolean
name|ignoreUnmapped
parameter_list|)
block|{
name|this
operator|.
name|ignoreUnampped
operator|=
name|ignoreUnmapped
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Defines what values to pick in the case a document contains multiple values for the targeted sort field.      * Possible values: min, max, sum and avg      *<p/>      * The last two values are only applicable for number based fields.      */
DECL|method|sortMode
specifier|public
name|FieldSortBuilder
name|sortMode
parameter_list|(
name|String
name|sortMode
parameter_list|)
block|{
name|this
operator|.
name|sortMode
operator|=
name|sortMode
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the nested filter that the nested objects should match with in order to be taken into account      * for sorting.      */
DECL|method|setNestedFilter
specifier|public
name|FieldSortBuilder
name|setNestedFilter
parameter_list|(
name|FilterBuilder
name|nestedFilter
parameter_list|)
block|{
name|this
operator|.
name|nestedFilter
operator|=
name|nestedFilter
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the nested path if sorting occurs on a field that is inside a nested object. By default when sorting on a      * field inside a nested object, the nearest upper nested object is selected as nested path.      */
DECL|method|setNestedPath
specifier|public
name|FieldSortBuilder
name|setNestedPath
parameter_list|(
name|String
name|nestedPath
parameter_list|)
block|{
name|this
operator|.
name|nestedPath
operator|=
name|nestedPath
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
if|if
condition|(
name|order
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"order"
argument_list|,
name|order
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|missing
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"missing"
argument_list|,
name|missing
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ignoreUnampped
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"ignore_unmapped"
argument_list|,
name|ignoreUnampped
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sortMode
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"mode"
argument_list|,
name|sortMode
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nestedFilter
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"nested_filter"
argument_list|,
name|nestedFilter
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nestedPath
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"nested_path"
argument_list|,
name|nestedPath
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

