begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.painless
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|painless
package|;
end_package

begin_comment
comment|/**  * The PainlessError class is used to throw internal errors caused by Painless scripts that cannot be  * caught using a standard {@link Exception}.  This prevents the user from catching this specific error  * (as Exceptions are available in the Painless API, but Errors are not,) and possibly continuing to do  * something hazardous.  The alternative was extending {@link Throwable}, but that seemed worse than using  * an {@link Error} in this case.  */
end_comment

begin_class
DECL|class|PainlessError
specifier|public
class|class
name|PainlessError
extends|extends
name|Error
block|{
comment|/**      * Constructor.      * @param message The error message.      */
DECL|method|PainlessError
specifier|public
name|PainlessError
parameter_list|(
specifier|final
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

