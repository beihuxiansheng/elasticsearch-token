begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.selector
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|selector
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
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
name|document
operator|.
name|StoredField
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
name|index
operator|.
name|FieldInfo
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
name|lucene
operator|.
name|document
operator|.
name|BaseFieldVisitor
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
name|mapper
operator|.
name|internal
operator|.
name|RoutingFieldMapper
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
name|mapper
operator|.
name|internal
operator|.
name|UidFieldMapper
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
comment|/**  * An optimized field selector that loads just the uid and the routing.  */
end_comment

begin_class
DECL|class|UidAndRoutingFieldVisitor
specifier|public
class|class
name|UidAndRoutingFieldVisitor
extends|extends
name|BaseFieldVisitor
block|{
DECL|field|uid
specifier|private
name|String
name|uid
decl_stmt|;
DECL|field|routing
specifier|private
name|String
name|routing
decl_stmt|;
annotation|@
name|Override
DECL|method|createDocument
specifier|public
name|Document
name|createDocument
parameter_list|()
block|{
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
literal|"uid"
argument_list|,
name|uid
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
literal|"_source"
argument_list|,
name|routing
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|document
return|;
block|}
annotation|@
name|Override
DECL|method|needsField
specifier|public
name|Status
name|needsField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|RoutingFieldMapper
operator|.
name|NAME
operator|.
name|equals
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|)
condition|)
block|{
return|return
name|Status
operator|.
name|YES
return|;
block|}
elseif|else
if|if
condition|(
name|UidFieldMapper
operator|.
name|NAME
operator|.
name|equals
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|)
condition|)
block|{
return|return
name|Status
operator|.
name|YES
return|;
block|}
return|return
name|uid
operator|!=
literal|null
operator|&&
name|routing
operator|!=
literal|null
condition|?
name|Status
operator|.
name|STOP
else|:
name|Status
operator|.
name|NO
return|;
block|}
annotation|@
name|Override
DECL|method|stringField
specifier|public
name|void
name|stringField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|RoutingFieldMapper
operator|.
name|NAME
operator|.
name|equals
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|)
condition|)
block|{
name|routing
operator|=
name|value
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|UidFieldMapper
operator|.
name|NAME
operator|.
name|equals
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|)
condition|)
block|{
name|uid
operator|=
name|value
expr_stmt|;
block|}
block|}
DECL|method|uid
specifier|public
name|String
name|uid
parameter_list|()
block|{
return|return
name|uid
return|;
block|}
DECL|method|routing
specifier|public
name|String
name|routing
parameter_list|()
block|{
return|return
name|routing
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"uid_and_routing"
return|;
block|}
block|}
end_class

end_unit

