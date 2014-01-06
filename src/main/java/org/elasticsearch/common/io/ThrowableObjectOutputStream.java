begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.io
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
package|;
end_package

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
name|ObjectOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectStreamClass
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|ThrowableObjectOutputStream
specifier|public
class|class
name|ThrowableObjectOutputStream
extends|extends
name|ObjectOutputStream
block|{
DECL|field|TYPE_FAT_DESCRIPTOR
specifier|static
specifier|final
name|int
name|TYPE_FAT_DESCRIPTOR
init|=
literal|0
decl_stmt|;
DECL|field|TYPE_THIN_DESCRIPTOR
specifier|static
specifier|final
name|int
name|TYPE_THIN_DESCRIPTOR
init|=
literal|1
decl_stmt|;
DECL|field|EXCEPTION_CLASSNAME
specifier|private
specifier|static
specifier|final
name|String
name|EXCEPTION_CLASSNAME
init|=
name|Exception
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
DECL|field|TYPE_EXCEPTION
specifier|static
specifier|final
name|int
name|TYPE_EXCEPTION
init|=
literal|2
decl_stmt|;
DECL|field|STACKTRACEELEMENT_CLASSNAME
specifier|private
specifier|static
specifier|final
name|String
name|STACKTRACEELEMENT_CLASSNAME
init|=
name|StackTraceElement
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
DECL|field|TYPE_STACKTRACEELEMENT
specifier|static
specifier|final
name|int
name|TYPE_STACKTRACEELEMENT
init|=
literal|3
decl_stmt|;
DECL|method|ThrowableObjectOutputStream
specifier|public
name|ThrowableObjectOutputStream
parameter_list|(
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeStreamHeader
specifier|protected
name|void
name|writeStreamHeader
parameter_list|()
throws|throws
name|IOException
block|{
name|writeByte
argument_list|(
name|STREAM_VERSION
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeClassDescriptor
specifier|protected
name|void
name|writeClassDescriptor
parameter_list|(
name|ObjectStreamClass
name|desc
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|desc
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|EXCEPTION_CLASSNAME
argument_list|)
condition|)
block|{
name|write
argument_list|(
name|TYPE_EXCEPTION
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|desc
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|STACKTRACEELEMENT_CLASSNAME
argument_list|)
condition|)
block|{
name|write
argument_list|(
name|TYPE_STACKTRACEELEMENT
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|desc
operator|.
name|forClass
argument_list|()
decl_stmt|;
if|if
condition|(
name|clazz
operator|.
name|isPrimitive
argument_list|()
operator|||
name|clazz
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|write
argument_list|(
name|TYPE_FAT_DESCRIPTOR
argument_list|)
expr_stmt|;
name|super
operator|.
name|writeClassDescriptor
argument_list|(
name|desc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|write
argument_list|(
name|TYPE_THIN_DESCRIPTOR
argument_list|)
expr_stmt|;
name|writeUTF
argument_list|(
name|desc
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

