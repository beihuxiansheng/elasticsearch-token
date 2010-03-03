begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.logging
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|logging
package|;
end_package

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Marker
import|;
end_import

begin_comment
comment|/**  * A Logger that wraps another logger and adds the provided prefix to every log  * message.  *  * @author kimchy (Shay Banon)  */
end_comment

begin_comment
comment|// TODO is there a way to do this without String concatenation?
end_comment

begin_class
DECL|class|PrefixLoggerAdapter
specifier|public
class|class
name|PrefixLoggerAdapter
implements|implements
name|Logger
block|{
DECL|field|prefix
specifier|private
specifier|final
name|String
name|prefix
decl_stmt|;
DECL|field|logger
specifier|private
specifier|final
name|Logger
name|logger
decl_stmt|;
DECL|method|PrefixLoggerAdapter
specifier|public
name|PrefixLoggerAdapter
parameter_list|(
name|String
name|prefix
parameter_list|,
name|Logger
name|logger
parameter_list|)
block|{
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
block|}
DECL|method|prefix
specifier|public
name|String
name|prefix
parameter_list|()
block|{
return|return
name|this
operator|.
name|prefix
return|;
block|}
DECL|method|wrap
specifier|private
name|String
name|wrap
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|prefix
operator|+
name|s
return|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|logger
operator|.
name|getName
argument_list|()
return|;
block|}
DECL|method|isTraceEnabled
specifier|public
specifier|final
name|boolean
name|isTraceEnabled
parameter_list|()
block|{
return|return
name|logger
operator|.
name|isTraceEnabled
argument_list|()
return|;
block|}
DECL|method|trace
specifier|public
specifier|final
name|void
name|trace
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|trace
specifier|public
specifier|final
name|void
name|trace
parameter_list|(
name|String
name|s
parameter_list|,
name|Object
name|o
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
DECL|method|trace
specifier|public
specifier|final
name|void
name|trace
parameter_list|(
name|String
name|s
parameter_list|,
name|Object
name|o
parameter_list|,
name|Object
name|o1
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|o
argument_list|,
name|o1
argument_list|)
expr_stmt|;
block|}
DECL|method|trace
specifier|public
specifier|final
name|void
name|trace
parameter_list|(
name|String
name|s
parameter_list|,
name|Object
index|[]
name|objects
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|objects
argument_list|)
expr_stmt|;
block|}
DECL|method|trace
specifier|public
specifier|final
name|void
name|trace
parameter_list|(
name|String
name|s
parameter_list|,
name|Throwable
name|throwable
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|throwable
argument_list|)
expr_stmt|;
block|}
DECL|method|isTraceEnabled
specifier|public
specifier|final
name|boolean
name|isTraceEnabled
parameter_list|(
name|Marker
name|marker
parameter_list|)
block|{
return|return
name|logger
operator|.
name|isTraceEnabled
argument_list|(
name|marker
argument_list|)
return|;
block|}
DECL|method|trace
specifier|public
specifier|final
name|void
name|trace
parameter_list|(
name|Marker
name|marker
parameter_list|,
name|String
name|s
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
name|marker
argument_list|,
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|trace
specifier|public
specifier|final
name|void
name|trace
parameter_list|(
name|Marker
name|marker
parameter_list|,
name|String
name|s
parameter_list|,
name|Object
name|o
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
name|marker
argument_list|,
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
DECL|method|trace
specifier|public
specifier|final
name|void
name|trace
parameter_list|(
name|Marker
name|marker
parameter_list|,
name|String
name|s
parameter_list|,
name|Object
name|o
parameter_list|,
name|Object
name|o1
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
name|marker
argument_list|,
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|o
argument_list|,
name|o1
argument_list|)
expr_stmt|;
block|}
DECL|method|trace
specifier|public
specifier|final
name|void
name|trace
parameter_list|(
name|Marker
name|marker
parameter_list|,
name|String
name|s
parameter_list|,
name|Object
index|[]
name|objects
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
name|marker
argument_list|,
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|objects
argument_list|)
expr_stmt|;
block|}
DECL|method|trace
specifier|public
specifier|final
name|void
name|trace
parameter_list|(
name|Marker
name|marker
parameter_list|,
name|String
name|s
parameter_list|,
name|Throwable
name|throwable
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
name|marker
argument_list|,
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|throwable
argument_list|)
expr_stmt|;
block|}
DECL|method|isDebugEnabled
specifier|public
specifier|final
name|boolean
name|isDebugEnabled
parameter_list|()
block|{
return|return
name|logger
operator|.
name|isDebugEnabled
argument_list|()
return|;
block|}
DECL|method|debug
specifier|public
specifier|final
name|void
name|debug
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|debug
specifier|public
specifier|final
name|void
name|debug
parameter_list|(
name|String
name|s
parameter_list|,
name|Object
name|o
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
DECL|method|debug
specifier|public
specifier|final
name|void
name|debug
parameter_list|(
name|String
name|s
parameter_list|,
name|Object
name|o
parameter_list|,
name|Object
name|o1
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|o
argument_list|,
name|o1
argument_list|)
expr_stmt|;
block|}
DECL|method|debug
specifier|public
specifier|final
name|void
name|debug
parameter_list|(
name|String
name|s
parameter_list|,
name|Object
index|[]
name|objects
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|objects
argument_list|)
expr_stmt|;
block|}
DECL|method|debug
specifier|public
specifier|final
name|void
name|debug
parameter_list|(
name|String
name|s
parameter_list|,
name|Throwable
name|throwable
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|throwable
argument_list|)
expr_stmt|;
block|}
DECL|method|isDebugEnabled
specifier|public
specifier|final
name|boolean
name|isDebugEnabled
parameter_list|(
name|Marker
name|marker
parameter_list|)
block|{
return|return
name|logger
operator|.
name|isDebugEnabled
argument_list|(
name|marker
argument_list|)
return|;
block|}
DECL|method|debug
specifier|public
specifier|final
name|void
name|debug
parameter_list|(
name|Marker
name|marker
parameter_list|,
name|String
name|s
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
name|marker
argument_list|,
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|debug
specifier|public
specifier|final
name|void
name|debug
parameter_list|(
name|Marker
name|marker
parameter_list|,
name|String
name|s
parameter_list|,
name|Object
name|o
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
name|marker
argument_list|,
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
DECL|method|debug
specifier|public
specifier|final
name|void
name|debug
parameter_list|(
name|Marker
name|marker
parameter_list|,
name|String
name|s
parameter_list|,
name|Object
name|o
parameter_list|,
name|Object
name|o1
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
name|marker
argument_list|,
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|o
argument_list|,
name|o1
argument_list|)
expr_stmt|;
block|}
DECL|method|debug
specifier|public
specifier|final
name|void
name|debug
parameter_list|(
name|Marker
name|marker
parameter_list|,
name|String
name|s
parameter_list|,
name|Object
index|[]
name|objects
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
name|marker
argument_list|,
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|objects
argument_list|)
expr_stmt|;
block|}
DECL|method|debug
specifier|public
specifier|final
name|void
name|debug
parameter_list|(
name|Marker
name|marker
parameter_list|,
name|String
name|s
parameter_list|,
name|Throwable
name|throwable
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
name|marker
argument_list|,
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|throwable
argument_list|)
expr_stmt|;
block|}
DECL|method|isInfoEnabled
specifier|public
specifier|final
name|boolean
name|isInfoEnabled
parameter_list|()
block|{
return|return
name|logger
operator|.
name|isInfoEnabled
argument_list|()
return|;
block|}
DECL|method|info
specifier|public
specifier|final
name|void
name|info
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|info
specifier|public
specifier|final
name|void
name|info
parameter_list|(
name|String
name|s
parameter_list|,
name|Object
name|o
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
DECL|method|info
specifier|public
specifier|final
name|void
name|info
parameter_list|(
name|String
name|s
parameter_list|,
name|Object
name|o
parameter_list|,
name|Object
name|o1
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|o
argument_list|,
name|o1
argument_list|)
expr_stmt|;
block|}
DECL|method|info
specifier|public
specifier|final
name|void
name|info
parameter_list|(
name|String
name|s
parameter_list|,
name|Object
index|[]
name|objects
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|objects
argument_list|)
expr_stmt|;
block|}
DECL|method|info
specifier|public
specifier|final
name|void
name|info
parameter_list|(
name|String
name|s
parameter_list|,
name|Throwable
name|throwable
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|throwable
argument_list|)
expr_stmt|;
block|}
DECL|method|isInfoEnabled
specifier|public
specifier|final
name|boolean
name|isInfoEnabled
parameter_list|(
name|Marker
name|marker
parameter_list|)
block|{
return|return
name|logger
operator|.
name|isInfoEnabled
argument_list|(
name|marker
argument_list|)
return|;
block|}
DECL|method|info
specifier|public
specifier|final
name|void
name|info
parameter_list|(
name|Marker
name|marker
parameter_list|,
name|String
name|s
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
name|marker
argument_list|,
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|info
specifier|public
specifier|final
name|void
name|info
parameter_list|(
name|Marker
name|marker
parameter_list|,
name|String
name|s
parameter_list|,
name|Object
name|o
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
name|marker
argument_list|,
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
DECL|method|info
specifier|public
specifier|final
name|void
name|info
parameter_list|(
name|Marker
name|marker
parameter_list|,
name|String
name|s
parameter_list|,
name|Object
name|o
parameter_list|,
name|Object
name|o1
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
name|marker
argument_list|,
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|o
argument_list|,
name|o1
argument_list|)
expr_stmt|;
block|}
DECL|method|info
specifier|public
specifier|final
name|void
name|info
parameter_list|(
name|Marker
name|marker
parameter_list|,
name|String
name|s
parameter_list|,
name|Object
index|[]
name|objects
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
name|marker
argument_list|,
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|objects
argument_list|)
expr_stmt|;
block|}
DECL|method|info
specifier|public
specifier|final
name|void
name|info
parameter_list|(
name|Marker
name|marker
parameter_list|,
name|String
name|s
parameter_list|,
name|Throwable
name|throwable
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
name|marker
argument_list|,
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|throwable
argument_list|)
expr_stmt|;
block|}
DECL|method|isWarnEnabled
specifier|public
specifier|final
name|boolean
name|isWarnEnabled
parameter_list|()
block|{
return|return
name|logger
operator|.
name|isWarnEnabled
argument_list|()
return|;
block|}
DECL|method|warn
specifier|public
specifier|final
name|void
name|warn
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|warn
specifier|public
specifier|final
name|void
name|warn
parameter_list|(
name|String
name|s
parameter_list|,
name|Object
name|o
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
DECL|method|warn
specifier|public
specifier|final
name|void
name|warn
parameter_list|(
name|String
name|s
parameter_list|,
name|Object
index|[]
name|objects
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|objects
argument_list|)
expr_stmt|;
block|}
DECL|method|warn
specifier|public
specifier|final
name|void
name|warn
parameter_list|(
name|String
name|s
parameter_list|,
name|Object
name|o
parameter_list|,
name|Object
name|o1
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|o
argument_list|,
name|o1
argument_list|)
expr_stmt|;
block|}
DECL|method|warn
specifier|public
specifier|final
name|void
name|warn
parameter_list|(
name|String
name|s
parameter_list|,
name|Throwable
name|throwable
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|throwable
argument_list|)
expr_stmt|;
block|}
DECL|method|isWarnEnabled
specifier|public
specifier|final
name|boolean
name|isWarnEnabled
parameter_list|(
name|Marker
name|marker
parameter_list|)
block|{
return|return
name|logger
operator|.
name|isWarnEnabled
argument_list|(
name|marker
argument_list|)
return|;
block|}
DECL|method|warn
specifier|public
specifier|final
name|void
name|warn
parameter_list|(
name|Marker
name|marker
parameter_list|,
name|String
name|s
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
name|marker
argument_list|,
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|warn
specifier|public
specifier|final
name|void
name|warn
parameter_list|(
name|Marker
name|marker
parameter_list|,
name|String
name|s
parameter_list|,
name|Object
name|o
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
name|marker
argument_list|,
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
DECL|method|warn
specifier|public
specifier|final
name|void
name|warn
parameter_list|(
name|Marker
name|marker
parameter_list|,
name|String
name|s
parameter_list|,
name|Object
name|o
parameter_list|,
name|Object
name|o1
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
name|marker
argument_list|,
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|o
argument_list|,
name|o1
argument_list|)
expr_stmt|;
block|}
DECL|method|warn
specifier|public
specifier|final
name|void
name|warn
parameter_list|(
name|Marker
name|marker
parameter_list|,
name|String
name|s
parameter_list|,
name|Object
index|[]
name|objects
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
name|marker
argument_list|,
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|objects
argument_list|)
expr_stmt|;
block|}
DECL|method|warn
specifier|public
specifier|final
name|void
name|warn
parameter_list|(
name|Marker
name|marker
parameter_list|,
name|String
name|s
parameter_list|,
name|Throwable
name|throwable
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
name|marker
argument_list|,
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|throwable
argument_list|)
expr_stmt|;
block|}
DECL|method|isErrorEnabled
specifier|public
specifier|final
name|boolean
name|isErrorEnabled
parameter_list|()
block|{
return|return
name|logger
operator|.
name|isErrorEnabled
argument_list|()
return|;
block|}
DECL|method|error
specifier|public
specifier|final
name|void
name|error
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|error
specifier|public
specifier|final
name|void
name|error
parameter_list|(
name|String
name|s
parameter_list|,
name|Object
name|o
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
DECL|method|error
specifier|public
specifier|final
name|void
name|error
parameter_list|(
name|String
name|s
parameter_list|,
name|Object
name|o
parameter_list|,
name|Object
name|o1
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|o
argument_list|,
name|o1
argument_list|)
expr_stmt|;
block|}
DECL|method|error
specifier|public
specifier|final
name|void
name|error
parameter_list|(
name|String
name|s
parameter_list|,
name|Object
index|[]
name|objects
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|objects
argument_list|)
expr_stmt|;
block|}
DECL|method|error
specifier|public
specifier|final
name|void
name|error
parameter_list|(
name|String
name|s
parameter_list|,
name|Throwable
name|throwable
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|throwable
argument_list|)
expr_stmt|;
block|}
DECL|method|isErrorEnabled
specifier|public
specifier|final
name|boolean
name|isErrorEnabled
parameter_list|(
name|Marker
name|marker
parameter_list|)
block|{
return|return
name|logger
operator|.
name|isErrorEnabled
argument_list|(
name|marker
argument_list|)
return|;
block|}
DECL|method|error
specifier|public
specifier|final
name|void
name|error
parameter_list|(
name|Marker
name|marker
parameter_list|,
name|String
name|s
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|marker
argument_list|,
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|error
specifier|public
specifier|final
name|void
name|error
parameter_list|(
name|Marker
name|marker
parameter_list|,
name|String
name|s
parameter_list|,
name|Object
name|o
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|marker
argument_list|,
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
DECL|method|error
specifier|public
specifier|final
name|void
name|error
parameter_list|(
name|Marker
name|marker
parameter_list|,
name|String
name|s
parameter_list|,
name|Object
name|o
parameter_list|,
name|Object
name|o1
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|marker
argument_list|,
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|o
argument_list|,
name|o1
argument_list|)
expr_stmt|;
block|}
DECL|method|error
specifier|public
specifier|final
name|void
name|error
parameter_list|(
name|Marker
name|marker
parameter_list|,
name|String
name|s
parameter_list|,
name|Object
index|[]
name|objects
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|marker
argument_list|,
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|objects
argument_list|)
expr_stmt|;
block|}
DECL|method|error
specifier|public
specifier|final
name|void
name|error
parameter_list|(
name|Marker
name|marker
parameter_list|,
name|String
name|s
parameter_list|,
name|Throwable
name|throwable
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|marker
argument_list|,
name|wrap
argument_list|(
name|s
argument_list|)
argument_list|,
name|throwable
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

