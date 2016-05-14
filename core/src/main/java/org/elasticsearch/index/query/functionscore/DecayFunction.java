begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query.functionscore
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|functionscore
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
name|search
operator|.
name|Explanation
import|;
end_import

begin_comment
comment|/**  * Implement this interface to provide a decay function that is executed on a  * distance. For example, this could be an exponential drop of, a triangle  * function or something of the kind. This is used, for example, by  * {@link GaussDecayFunctionBuilder}.  *   */
end_comment

begin_interface
DECL|interface|DecayFunction
specifier|public
interface|interface
name|DecayFunction
block|{
DECL|method|evaluate
name|double
name|evaluate
parameter_list|(
name|double
name|value
parameter_list|,
name|double
name|scale
parameter_list|)
function_decl|;
DECL|method|explainFunction
name|Explanation
name|explainFunction
parameter_list|(
name|String
name|valueString
parameter_list|,
name|double
name|value
parameter_list|,
name|double
name|scale
parameter_list|)
function_decl|;
comment|/**      * The final scale parameter is computed from the scale parameter given by      * the user and a value. This value is the value that the decay function      * should compute if document distance and user defined scale equal. The      * scale parameter for the function must be adjusted accordingly in this      * function      *       * @param scale      *            the raw scale value given by the user      * @param decay      *            the value which decay function should take once the distance      *            reaches this scale      * */
DECL|method|processScale
name|double
name|processScale
parameter_list|(
name|double
name|scale
parameter_list|,
name|double
name|decay
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

