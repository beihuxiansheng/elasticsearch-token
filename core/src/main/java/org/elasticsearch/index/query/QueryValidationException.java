begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
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
name|ValidationException
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

begin_comment
comment|/**  * This exception can be used to indicate various reasons why validation of a query has failed.  */
end_comment

begin_class
DECL|class|QueryValidationException
specifier|public
class|class
name|QueryValidationException
extends|extends
name|ValidationException
block|{
comment|/**      * Helper method than can be used to add error messages to an existing {@link QueryValidationException}.      * When passing {@code null} as the initial exception, a new exception is created.      *      * @param queryId the query that caused the error      * @param validationError the error message to add to an initial exception      * @param validationException an initial exception. Can be {@code null}, in which case a new exception is created.      * @return a {@link QueryValidationException} with added validation error message      */
DECL|method|addValidationError
specifier|public
specifier|static
name|QueryValidationException
name|addValidationError
parameter_list|(
name|String
name|queryId
parameter_list|,
name|String
name|validationError
parameter_list|,
name|QueryValidationException
name|validationException
parameter_list|)
block|{
if|if
condition|(
name|validationException
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
operator|new
name|QueryValidationException
argument_list|()
expr_stmt|;
block|}
name|validationException
operator|.
name|addValidationError
argument_list|(
literal|"["
operator|+
name|queryId
operator|+
literal|"] "
operator|+
name|validationError
argument_list|)
expr_stmt|;
return|return
name|validationException
return|;
block|}
comment|/**      * Helper method than can be used to add error messages to an existing {@link QueryValidationException}.      * When passing {@code null} as the initial exception, a new exception is created.      * @param validationErrors the error messages to add to an initial exception      * @param validationException an initial exception. Can be {@code null}, in which case a new exception is created.      * @return a {@link QueryValidationException} with added validation error message      */
DECL|method|addValidationErrors
specifier|public
specifier|static
name|QueryValidationException
name|addValidationErrors
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|validationErrors
parameter_list|,
name|QueryValidationException
name|validationException
parameter_list|)
block|{
if|if
condition|(
name|validationException
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
operator|new
name|QueryValidationException
argument_list|()
expr_stmt|;
block|}
name|validationException
operator|.
name|addValidationErrors
argument_list|(
name|validationErrors
argument_list|)
expr_stmt|;
return|return
name|validationException
return|;
block|}
block|}
end_class

end_unit

