begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.lookup
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|lookup
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|MappedFieldType
import|;
end_import

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
name|Map
import|;
end_import

begin_class
DECL|class|FieldLookup
specifier|public
class|class
name|FieldLookup
block|{
comment|// we can cached fieldType completely per name, since its on an index/shard level (the lookup, and it does not change within the scope of a search request)
DECL|field|fieldType
specifier|private
specifier|final
name|MappedFieldType
name|fieldType
decl_stmt|;
DECL|field|fields
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Object
argument_list|>
argument_list|>
name|fields
decl_stmt|;
DECL|field|value
specifier|private
name|Object
name|value
decl_stmt|;
DECL|field|valueLoaded
specifier|private
name|boolean
name|valueLoaded
init|=
literal|false
decl_stmt|;
DECL|field|values
specifier|private
name|List
argument_list|<
name|Object
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|valuesLoaded
specifier|private
name|boolean
name|valuesLoaded
init|=
literal|false
decl_stmt|;
DECL|method|FieldLookup
name|FieldLookup
parameter_list|(
name|MappedFieldType
name|fieldType
parameter_list|)
block|{
name|this
operator|.
name|fieldType
operator|=
name|fieldType
expr_stmt|;
block|}
DECL|method|fieldType
specifier|public
name|MappedFieldType
name|fieldType
parameter_list|()
block|{
return|return
name|fieldType
return|;
block|}
DECL|method|fields
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Object
argument_list|>
argument_list|>
name|fields
parameter_list|()
block|{
return|return
name|fields
return|;
block|}
comment|/**      * Sets the post processed values.      */
DECL|method|fields
specifier|public
name|void
name|fields
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Object
argument_list|>
argument_list|>
name|fields
parameter_list|)
block|{
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|value
operator|=
literal|null
expr_stmt|;
name|valueLoaded
operator|=
literal|false
expr_stmt|;
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|valuesLoaded
operator|=
literal|false
expr_stmt|;
name|fields
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|isEmpty
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
if|if
condition|(
name|valueLoaded
condition|)
block|{
return|return
name|value
operator|==
literal|null
return|;
block|}
if|if
condition|(
name|valuesLoaded
condition|)
block|{
return|return
name|values
operator|.
name|isEmpty
argument_list|()
return|;
block|}
return|return
name|getValue
argument_list|()
operator|==
literal|null
return|;
block|}
DECL|method|getValue
specifier|public
name|Object
name|getValue
parameter_list|()
block|{
if|if
condition|(
name|valueLoaded
condition|)
block|{
return|return
name|value
return|;
block|}
name|valueLoaded
operator|=
literal|true
expr_stmt|;
name|value
operator|=
literal|null
expr_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|values
init|=
name|fields
operator|.
name|get
argument_list|(
name|fieldType
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|values
operator|!=
literal|null
condition|?
name|value
operator|=
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
else|:
literal|null
return|;
block|}
DECL|method|getValues
specifier|public
name|List
argument_list|<
name|Object
argument_list|>
name|getValues
parameter_list|()
block|{
if|if
condition|(
name|valuesLoaded
condition|)
block|{
return|return
name|values
return|;
block|}
name|valuesLoaded
operator|=
literal|true
expr_stmt|;
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|values
operator|=
name|fields
argument_list|()
operator|.
name|get
argument_list|(
name|fieldType
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

