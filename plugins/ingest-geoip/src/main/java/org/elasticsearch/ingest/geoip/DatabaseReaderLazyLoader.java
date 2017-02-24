begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.ingest.geoip
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|geoip
package|;
end_package

begin_import
import|import
name|com
operator|.
name|maxmind
operator|.
name|geoip2
operator|.
name|DatabaseReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|SetOnce
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
name|CheckedSupplier
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
name|logging
operator|.
name|Loggers
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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

begin_comment
comment|/**  * Facilitates lazy loading of the database reader, so that when the geoip plugin is installed, but not used,  * no memory is being wasted on the database reader.  */
end_comment

begin_class
DECL|class|DatabaseReaderLazyLoader
specifier|final
class|class
name|DatabaseReaderLazyLoader
implements|implements
name|Closeable
block|{
DECL|field|LOGGER
specifier|private
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|DatabaseReaderLazyLoader
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|databaseFileName
specifier|private
specifier|final
name|String
name|databaseFileName
decl_stmt|;
DECL|field|loader
specifier|private
specifier|final
name|CheckedSupplier
argument_list|<
name|DatabaseReader
argument_list|,
name|IOException
argument_list|>
name|loader
decl_stmt|;
comment|// package protected for testing only:
DECL|field|databaseReader
specifier|final
name|SetOnce
argument_list|<
name|DatabaseReader
argument_list|>
name|databaseReader
decl_stmt|;
DECL|method|DatabaseReaderLazyLoader
name|DatabaseReaderLazyLoader
parameter_list|(
name|String
name|databaseFileName
parameter_list|,
name|CheckedSupplier
argument_list|<
name|DatabaseReader
argument_list|,
name|IOException
argument_list|>
name|loader
parameter_list|)
block|{
name|this
operator|.
name|databaseFileName
operator|=
name|databaseFileName
expr_stmt|;
name|this
operator|.
name|loader
operator|=
name|loader
expr_stmt|;
name|this
operator|.
name|databaseReader
operator|=
operator|new
name|SetOnce
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|get
specifier|synchronized
name|DatabaseReader
name|get
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|databaseReader
operator|.
name|get
argument_list|()
operator|==
literal|null
condition|)
block|{
name|databaseReader
operator|.
name|set
argument_list|(
name|loader
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"Loaded [{}] geoip database"
argument_list|,
name|databaseFileName
argument_list|)
expr_stmt|;
block|}
return|return
name|databaseReader
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|databaseReader
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

