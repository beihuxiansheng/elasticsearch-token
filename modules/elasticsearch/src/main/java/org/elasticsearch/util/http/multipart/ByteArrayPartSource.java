begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright 2010 Ning, Inc.  *  * Ning licenses this file to you under the Apache License, version 2.0  * (the "License"); you may not use this file except in compliance with the  * License.  You may obtain a copy of the License at:  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the  * License for the specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.http.multipart
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|http
operator|.
name|multipart
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

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
name|InputStream
import|;
end_import

begin_comment
comment|/**  * This class is an adaptation of the Apache HttpClient implementation  *  * @link http://hc.apache.org/httpclient-3.x/  */
end_comment

begin_class
DECL|class|ByteArrayPartSource
specifier|public
class|class
name|ByteArrayPartSource
implements|implements
name|PartSource
block|{
comment|/**      * Name of the source file.      */
DECL|field|fileName
specifier|private
name|String
name|fileName
decl_stmt|;
comment|/**      * Byte array of the source file.      */
DECL|field|bytes
specifier|private
name|byte
index|[]
name|bytes
decl_stmt|;
comment|/**      * Constructor for ByteArrayPartSource.      *      * @param fileName the name of the file these bytes represent      * @param bytes    the content of this part      */
DECL|method|ByteArrayPartSource
specifier|public
name|ByteArrayPartSource
parameter_list|(
name|String
name|fileName
parameter_list|,
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|this
operator|.
name|fileName
operator|=
name|fileName
expr_stmt|;
name|this
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
block|}
comment|/**      * @see PartSource#getLength()      */
DECL|method|getLength
specifier|public
name|long
name|getLength
parameter_list|()
block|{
return|return
name|bytes
operator|.
name|length
return|;
block|}
comment|/**      * @see PartSource#getFileName()      */
DECL|method|getFileName
specifier|public
name|String
name|getFileName
parameter_list|()
block|{
return|return
name|fileName
return|;
block|}
comment|/**      * @see PartSource#createInputStream()      */
DECL|method|createInputStream
specifier|public
name|InputStream
name|createInputStream
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
return|;
block|}
block|}
end_class

end_unit

