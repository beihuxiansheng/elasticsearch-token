begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.gnu.trove
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|gnu
operator|.
name|trove
package|;
end_package

begin_comment
comment|//////////////////////////////////////////////////
end_comment

begin_comment
comment|// THIS IS A GENERATED CLASS. DO NOT HAND EDIT! //
end_comment

begin_comment
comment|//////////////////////////////////////////////////
end_comment

begin_comment
comment|/**  * Interface for procedures that take two parameters of type Object and float.  *<p/>  * Created: Mon Nov  5 22:03:30 2001  *  * @author Eric D. Friedman  * @version $Id: O2PProcedure.template,v 1.1 2006/11/10 23:28:00 robeden Exp $  */
end_comment

begin_interface
DECL|interface|TObjectFloatProcedure
specifier|public
interface|interface
name|TObjectFloatProcedure
parameter_list|<
name|K
parameter_list|>
block|{
comment|/**      * Executes this procedure. A false return value indicates that      * the application executing this procedure should not invoke this      * procedure again.      *      * @param a an<code>Object</code> value      * @param b a<code>float</code> value      * @return true if additional invocations of the procedure are      *         allowed.      */
DECL|method|execute
specifier|public
name|boolean
name|execute
parameter_list|(
name|K
name|a
parameter_list|,
name|float
name|b
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// TObjectFloatProcedure
end_comment

end_unit

