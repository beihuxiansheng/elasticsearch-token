begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.postingshighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|postingshighlight
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
import|;
end_import

begin_comment
comment|/**  * Creates a formatted snippet from the top passages.  *  * @lucene.experimental  */
end_comment

begin_comment
comment|//LUCENE MONITOR - REMOVE ME WHEN LUCENE 4.6 IS OUT
end_comment

begin_comment
comment|//Applied LUCENE-4906 to be able to return arbitrary objects
end_comment

begin_class
DECL|class|XPassageFormatter
specifier|public
specifier|abstract
class|class
name|XPassageFormatter
block|{
static|static
block|{
assert|assert
name|Version
operator|.
name|CURRENT
operator|.
name|luceneVersion
operator|.
name|compareTo
argument_list|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
operator|.
name|LUCENE_45
argument_list|)
operator|==
literal|0
operator|:
literal|"Remove XPassageFormatter once 4.6 is out"
assert|;
block|}
comment|/**      * Formats the top<code>passages</code> from<code>content</code>      * into a human-readable text snippet.      *      * @param passages top-N passages for the field. Note these are sorted in      *        the order that they appear in the document for convenience.      * @param content content for the field.      * @return formatted highlight      */
DECL|method|format
specifier|public
specifier|abstract
name|Object
name|format
parameter_list|(
name|Passage
name|passages
index|[]
parameter_list|,
name|String
name|content
parameter_list|)
function_decl|;
block|}
end_class

end_unit

