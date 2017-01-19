begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.xcontent
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
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
name|Booleans
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
name|bytes
operator|.
name|BytesReference
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * A generic abstraction on top of handling content, inspired by JSON and pull parsing.  */
end_comment

begin_interface
DECL|interface|XContent
specifier|public
interface|interface
name|XContent
block|{
comment|/*      * NOTE: This comment is only meant for maintainers of the Elasticsearch code base and is intentionally not a Javadoc comment as it      *       describes an undocumented system property.      *      *      * Determines whether the XContent parser will always check for duplicate keys. This behavior is enabled by default but      * can be disabled by setting the otherwise undocumented system property "es.xcontent.strict_duplicate_detection to "false".      *      * Before we've enabled this mode, we had custom duplicate checks in various parts of the code base. As the user can still disable this      * mode and fall back to the legacy duplicate checks, we still need to keep the custom duplicate checks around and we also need to keep      * the tests around.      *      * If this fallback via system property is removed one day in the future you can remove all tests that call this method and also remove      * the corresponding custom duplicate check code.      *      */
DECL|method|isStrictDuplicateDetectionEnabled
specifier|static
name|boolean
name|isStrictDuplicateDetectionEnabled
parameter_list|()
block|{
comment|// Don't allow duplicate keys in JSON content by default but let the user opt out
return|return
name|Booleans
operator|.
name|parseBoolean
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"es.xcontent.strict_duplicate_detection"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * The type this content handles and produces.      */
DECL|method|type
name|XContentType
name|type
parameter_list|()
function_decl|;
DECL|method|streamSeparator
name|byte
name|streamSeparator
parameter_list|()
function_decl|;
comment|/**      * Creates a new generator using the provided output stream.      */
DECL|method|createGenerator
specifier|default
name|XContentGenerator
name|createGenerator
parameter_list|(
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|createGenerator
argument_list|(
name|os
argument_list|,
name|Collections
operator|.
name|emptySet
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptySet
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Creates a new generator using the provided output stream and some inclusive and/or exclusive filters. When both exclusive and      * inclusive filters are provided, the underlying generator will first use exclusion filters to remove fields and then will check the      * remaining fields against the inclusive filters.      *      * @param os       the output stream      * @param includes the inclusive filters: only fields and objects that match the inclusive filters will be written to the output.      * @param excludes the exclusive filters: only fields and objects that don't match the exclusive filters will be written to the output.      */
DECL|method|createGenerator
name|XContentGenerator
name|createGenerator
parameter_list|(
name|OutputStream
name|os
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|includes
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|excludes
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Creates a parser over the provided string content.      */
DECL|method|createParser
name|XContentParser
name|createParser
parameter_list|(
name|NamedXContentRegistry
name|xContentRegistry
parameter_list|,
name|String
name|content
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Creates a parser over the provided input stream.      */
DECL|method|createParser
name|XContentParser
name|createParser
parameter_list|(
name|NamedXContentRegistry
name|xContentRegistry
parameter_list|,
name|InputStream
name|is
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Creates a parser over the provided bytes.      */
DECL|method|createParser
name|XContentParser
name|createParser
parameter_list|(
name|NamedXContentRegistry
name|xContentRegistry
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Creates a parser over the provided bytes.      */
DECL|method|createParser
name|XContentParser
name|createParser
parameter_list|(
name|NamedXContentRegistry
name|xContentRegistry
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Creates a parser over the provided bytes.      */
DECL|method|createParser
name|XContentParser
name|createParser
parameter_list|(
name|NamedXContentRegistry
name|xContentRegistry
parameter_list|,
name|BytesReference
name|bytes
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Creates a parser over the provided reader.      */
DECL|method|createParser
name|XContentParser
name|createParser
parameter_list|(
name|NamedXContentRegistry
name|xContentRegistry
parameter_list|,
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

