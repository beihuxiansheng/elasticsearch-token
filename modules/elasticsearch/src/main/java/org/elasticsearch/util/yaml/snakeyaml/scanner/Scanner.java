begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.yaml.snakeyaml.scanner
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|scanner
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|tokens
operator|.
name|Token
import|;
end_import

begin_comment
comment|/**  * This interface represents an input stream of {@link Token Tokens}.  *<p>  * The parser and the scanner form together the 'Parse' step in the loading  * process (see chapter 3.1 of the<a href="http://yaml.org/spec/1.1/">YAML  * Specification</a>).  *</p>  *  * @see org.elasticsearch.util.yaml.snakeyaml.tokens.Token  */
end_comment

begin_interface
DECL|interface|Scanner
specifier|public
interface|interface
name|Scanner
block|{
comment|/**      * Check if the next token is one of the given types.      *      * @param choices token IDs.      * @return<code>true</code> if the next token can be assigned to a variable      *         of at least one of the given types. Returns<code>false</code> if      *         no more tokens are available.      * @throws ScannerException Thrown in case of malformed input.      */
DECL|method|checkToken
name|boolean
name|checkToken
parameter_list|(
name|Token
operator|.
name|ID
modifier|...
name|choices
parameter_list|)
function_decl|;
comment|/**      * Return the next token, but do not delete it from the stream.      *      * @return The token that will be returned on the next call to      *         {@link #getToken}      * @throws ScannerException Thrown in case of malformed input.      */
DECL|method|peekToken
name|Token
name|peekToken
parameter_list|()
function_decl|;
comment|/**      * Returns the next token.      *<p>      * The token will be removed from the stream.      *</p>      *      * @throws ScannerException Thrown in case of malformed input.      */
DECL|method|getToken
name|Token
name|getToken
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

