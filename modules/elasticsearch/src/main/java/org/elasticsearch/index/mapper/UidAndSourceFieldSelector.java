begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
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
name|FieldSelector
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
name|FieldSelectorResult
import|;
end_import

begin_comment
comment|/**  * An optimized field selector that loads just the uid and the source.  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|UidAndSourceFieldSelector
specifier|public
class|class
name|UidAndSourceFieldSelector
implements|implements
name|FieldSelector
block|{
DECL|field|match
specifier|private
name|int
name|match
init|=
literal|0
decl_stmt|;
DECL|method|accept
annotation|@
name|Override
specifier|public
name|FieldSelectorResult
name|accept
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
if|if
condition|(
name|UidFieldMapper
operator|.
name|NAME
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
if|if
condition|(
operator|++
name|match
operator|==
literal|2
condition|)
block|{
name|match
operator|=
literal|0
expr_stmt|;
return|return
name|FieldSelectorResult
operator|.
name|LOAD_AND_BREAK
return|;
block|}
return|return
name|FieldSelectorResult
operator|.
name|LOAD
return|;
block|}
if|if
condition|(
name|SourceFieldMapper
operator|.
name|NAME
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
if|if
condition|(
operator|++
name|match
operator|==
literal|2
condition|)
block|{
name|match
operator|=
literal|0
expr_stmt|;
return|return
name|FieldSelectorResult
operator|.
name|LOAD_AND_BREAK
return|;
block|}
return|return
name|FieldSelectorResult
operator|.
name|LOAD
return|;
block|}
return|return
name|FieldSelectorResult
operator|.
name|NO_LOAD
return|;
block|}
block|}
end_class

end_unit

