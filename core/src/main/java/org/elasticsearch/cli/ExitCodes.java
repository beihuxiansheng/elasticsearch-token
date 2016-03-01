begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cli
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cli
package|;
end_package

begin_comment
comment|/**  * POSIX exit codes.  */
end_comment

begin_class
DECL|class|ExitCodes
specifier|public
class|class
name|ExitCodes
block|{
DECL|field|OK
specifier|public
specifier|static
specifier|final
name|int
name|OK
init|=
literal|0
decl_stmt|;
DECL|field|USAGE
specifier|public
specifier|static
specifier|final
name|int
name|USAGE
init|=
literal|64
decl_stmt|;
comment|/* command line usage error */
DECL|field|DATA_ERROR
specifier|public
specifier|static
specifier|final
name|int
name|DATA_ERROR
init|=
literal|65
decl_stmt|;
comment|/* data format error */
DECL|field|NO_INPUT
specifier|public
specifier|static
specifier|final
name|int
name|NO_INPUT
init|=
literal|66
decl_stmt|;
comment|/* cannot open input */
DECL|field|NO_USER
specifier|public
specifier|static
specifier|final
name|int
name|NO_USER
init|=
literal|67
decl_stmt|;
comment|/* addressee unknown */
DECL|field|NO_HOST
specifier|public
specifier|static
specifier|final
name|int
name|NO_HOST
init|=
literal|68
decl_stmt|;
comment|/* host name unknown */
DECL|field|UNAVAILABLE
specifier|public
specifier|static
specifier|final
name|int
name|UNAVAILABLE
init|=
literal|69
decl_stmt|;
comment|/* service unavailable */
DECL|field|CODE_ERROR
specifier|public
specifier|static
specifier|final
name|int
name|CODE_ERROR
init|=
literal|70
decl_stmt|;
comment|/* internal software error */
DECL|field|CANT_CREATE
specifier|public
specifier|static
specifier|final
name|int
name|CANT_CREATE
init|=
literal|73
decl_stmt|;
comment|/* can't create (user) output file */
DECL|field|IO_ERROR
specifier|public
specifier|static
specifier|final
name|int
name|IO_ERROR
init|=
literal|74
decl_stmt|;
comment|/* input/output error */
DECL|field|TEMP_FAILURE
specifier|public
specifier|static
specifier|final
name|int
name|TEMP_FAILURE
init|=
literal|75
decl_stmt|;
comment|/* temp failure; user is invited to retry */
DECL|field|PROTOCOL
specifier|public
specifier|static
specifier|final
name|int
name|PROTOCOL
init|=
literal|76
decl_stmt|;
comment|/* remote error in protocol */
DECL|field|NOPERM
specifier|public
specifier|static
specifier|final
name|int
name|NOPERM
init|=
literal|77
decl_stmt|;
comment|/* permission denied */
DECL|field|CONFIG
specifier|public
specifier|static
specifier|final
name|int
name|CONFIG
init|=
literal|78
decl_stmt|;
comment|/* configuration error */
DECL|method|ExitCodes
specifier|private
name|ExitCodes
parameter_list|()
block|{
comment|/* no instance, just constants */
block|}
block|}
end_class

end_unit

