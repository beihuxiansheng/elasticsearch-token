begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|core
operator|.
name|AbstractFieldMapper
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
comment|/**  *  */
end_comment

begin_interface
DECL|interface|FieldMapper
specifier|public
interface|interface
name|FieldMapper
extends|extends
name|Mapper
block|{
DECL|field|DOC_VALUES_FORMAT
name|String
name|DOC_VALUES_FORMAT
init|=
literal|"doc_values_format"
decl_stmt|;
DECL|method|fieldType
name|MappedFieldType
name|fieldType
parameter_list|()
function_decl|;
comment|/**      * List of fields where this field should be copied to      */
DECL|method|copyTo
name|AbstractFieldMapper
operator|.
name|CopyTo
name|copyTo
parameter_list|()
function_decl|;
comment|/**      * Fields might not be available before indexing, for example _all, token_count,...      * When get is called and these fields are requested, this case needs special treatment.      *      * @return If the field is available before indexing or not.      * */
DECL|method|isGenerated
name|boolean
name|isGenerated
parameter_list|()
function_decl|;
comment|/**      * Parse using the provided {@link ParseContext} and return a mapping      * update if dynamic mappings modified the mappings, or {@code null} if      * mappings were not modified.      */
DECL|method|parse
name|Mapper
name|parse
parameter_list|(
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

