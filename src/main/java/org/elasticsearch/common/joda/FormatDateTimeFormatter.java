begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.joda
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|joda
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
name|util
operator|.
name|concurrent
operator|.
name|Immutable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|format
operator|.
name|DateTimeFormatter
import|;
end_import

begin_comment
comment|/**  * A simple wrapper around {@link DateTimeFormatter} that retains the  * format that was used to create it.  *  *  */
end_comment

begin_class
annotation|@
name|Immutable
DECL|class|FormatDateTimeFormatter
specifier|public
class|class
name|FormatDateTimeFormatter
block|{
DECL|field|format
specifier|private
specifier|final
name|String
name|format
decl_stmt|;
DECL|field|parser
specifier|private
specifier|final
name|DateTimeFormatter
name|parser
decl_stmt|;
DECL|field|printer
specifier|private
specifier|final
name|DateTimeFormatter
name|printer
decl_stmt|;
DECL|method|FormatDateTimeFormatter
specifier|public
name|FormatDateTimeFormatter
parameter_list|(
name|String
name|format
parameter_list|,
name|DateTimeFormatter
name|parser
parameter_list|)
block|{
name|this
argument_list|(
name|format
argument_list|,
name|parser
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
DECL|method|FormatDateTimeFormatter
specifier|public
name|FormatDateTimeFormatter
parameter_list|(
name|String
name|format
parameter_list|,
name|DateTimeFormatter
name|parser
parameter_list|,
name|DateTimeFormatter
name|printer
parameter_list|)
block|{
name|this
operator|.
name|format
operator|=
name|format
expr_stmt|;
name|this
operator|.
name|parser
operator|=
name|parser
expr_stmt|;
name|this
operator|.
name|printer
operator|=
name|printer
expr_stmt|;
block|}
DECL|method|format
specifier|public
name|String
name|format
parameter_list|()
block|{
return|return
name|format
return|;
block|}
DECL|method|parser
specifier|public
name|DateTimeFormatter
name|parser
parameter_list|()
block|{
return|return
name|parser
return|;
block|}
DECL|method|printer
specifier|public
name|DateTimeFormatter
name|printer
parameter_list|()
block|{
return|return
name|this
operator|.
name|printer
return|;
block|}
block|}
end_class

end_unit

