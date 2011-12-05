begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright (C) 2008 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.inject
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
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
name|inject
operator|.
name|internal
operator|.
name|Errors
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
name|inject
operator|.
name|spi
operator|.
name|Message
import|;
end_import

begin_comment
comment|/**  * Handles {@link Binder#addError} commands.  *  * @author crazybob@google.com (Bob Lee)  * @author jessewilson@google.com (Jesse Wilson)  */
end_comment

begin_class
DECL|class|MessageProcessor
class|class
name|MessageProcessor
extends|extends
name|AbstractProcessor
block|{
comment|//private static final Logger logger = Logger.getLogger(Guice.class.getName());
DECL|method|MessageProcessor
name|MessageProcessor
parameter_list|(
name|Errors
name|errors
parameter_list|)
block|{
name|super
argument_list|(
name|errors
argument_list|)
expr_stmt|;
block|}
DECL|method|visit
annotation|@
name|Override
specifier|public
name|Boolean
name|visit
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
comment|// ES_GUICE: don't log failures using jdk logging
comment|//        if (message.getCause() != null) {
comment|//            String rootMessage = getRootMessage(message.getCause());
comment|//            logger.log(Level.INFO,
comment|//                    "An exception was caught and reported. Message: " + rootMessage,
comment|//                    message.getCause());
comment|//        }
name|errors
operator|.
name|addMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|getRootMessage
specifier|public
specifier|static
name|String
name|getRootMessage
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|Throwable
name|cause
init|=
name|t
operator|.
name|getCause
argument_list|()
decl_stmt|;
return|return
name|cause
operator|==
literal|null
condition|?
name|t
operator|.
name|toString
argument_list|()
else|:
name|getRootMessage
argument_list|(
name|cause
argument_list|)
return|;
block|}
block|}
end_class

end_unit

