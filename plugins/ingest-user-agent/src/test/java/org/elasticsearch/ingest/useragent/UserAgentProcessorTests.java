begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.ingest.useragent
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|useragent
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|RandomDocumentPicks
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|IngestDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|useragent
operator|.
name|UserAgentProcessor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|hasKey
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|is
import|;
end_import

begin_class
DECL|class|UserAgentProcessorTests
specifier|public
class|class
name|UserAgentProcessorTests
extends|extends
name|ESTestCase
block|{
DECL|field|processor
specifier|private
specifier|static
name|UserAgentProcessor
name|processor
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setupProcessor
specifier|public
specifier|static
name|void
name|setupProcessor
parameter_list|()
throws|throws
name|IOException
block|{
name|InputStream
name|regexStream
init|=
name|UserAgentProcessor
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/regexes.yaml"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|regexStream
argument_list|)
expr_stmt|;
name|UserAgentParser
name|parser
init|=
operator|new
name|UserAgentParser
argument_list|(
name|randomAsciiOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
name|regexStream
argument_list|,
operator|new
name|UserAgentCache
argument_list|(
literal|1000
argument_list|)
argument_list|)
decl_stmt|;
name|processor
operator|=
operator|new
name|UserAgentProcessor
argument_list|(
name|randomAsciiOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
literal|"source_field"
argument_list|,
literal|"target_field"
argument_list|,
name|parser
argument_list|,
name|EnumSet
operator|.
name|allOf
argument_list|(
name|UserAgentProcessor
operator|.
name|Property
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testCommonBrowser
specifier|public
name|void
name|testCommonBrowser
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|document
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|document
operator|.
name|put
argument_list|(
literal|"source_field"
argument_list|,
literal|"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.149 Safari/537.36"
argument_list|)
expr_stmt|;
name|IngestDocument
name|ingestDocument
init|=
name|RandomDocumentPicks
operator|.
name|randomIngestDocument
argument_list|(
name|random
argument_list|()
argument_list|,
name|document
argument_list|)
decl_stmt|;
name|processor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|data
init|=
name|ingestDocument
operator|.
name|getSourceAndMetadata
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|data
argument_list|,
name|hasKey
argument_list|(
literal|"target_field"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|target
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|data
operator|.
name|get
argument_list|(
literal|"target_field"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"Chrome"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"major"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"33"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"minor"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"patch"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"1750"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"build"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"os"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"Mac OS X 10.9.2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"os_name"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"Mac OS X"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"os_major"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"10"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"os_minor"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"9"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"device"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"Other"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testUncommonDevice
specifier|public
name|void
name|testUncommonDevice
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|document
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|document
operator|.
name|put
argument_list|(
literal|"source_field"
argument_list|,
literal|"Mozilla/5.0 (Linux; U; Android 3.0; en-us; Xoom Build/HRI39) AppleWebKit/525.10+ "
operator|+
literal|"(KHTML, like Gecko) Version/3.0.4 Mobile Safari/523.12.2"
argument_list|)
expr_stmt|;
name|IngestDocument
name|ingestDocument
init|=
name|RandomDocumentPicks
operator|.
name|randomIngestDocument
argument_list|(
name|random
argument_list|()
argument_list|,
name|document
argument_list|)
decl_stmt|;
name|processor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|data
init|=
name|ingestDocument
operator|.
name|getSourceAndMetadata
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|data
argument_list|,
name|hasKey
argument_list|(
literal|"target_field"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|target
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|data
operator|.
name|get
argument_list|(
literal|"target_field"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"Android"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"major"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"minor"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"patch"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"build"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"os"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"Android 3.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"os_name"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"Android"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"os_major"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"os_minor"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"device"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"Motorola Xoom"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testSpider
specifier|public
name|void
name|testSpider
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|document
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|document
operator|.
name|put
argument_list|(
literal|"source_field"
argument_list|,
literal|"Mozilla/5.0 (compatible; EasouSpider; +http://www.easou.com/search/spider.html)"
argument_list|)
expr_stmt|;
name|IngestDocument
name|ingestDocument
init|=
name|RandomDocumentPicks
operator|.
name|randomIngestDocument
argument_list|(
name|random
argument_list|()
argument_list|,
name|document
argument_list|)
decl_stmt|;
name|processor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|data
init|=
name|ingestDocument
operator|.
name|getSourceAndMetadata
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|data
argument_list|,
name|hasKey
argument_list|(
literal|"target_field"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|target
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|data
operator|.
name|get
argument_list|(
literal|"target_field"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"EasouSpider"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"major"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"minor"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"patch"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"build"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"os"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"Other"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"os_name"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"Other"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"os_major"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"os_minor"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"device"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"Spider"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testUnknown
specifier|public
name|void
name|testUnknown
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|document
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|document
operator|.
name|put
argument_list|(
literal|"source_field"
argument_list|,
literal|"Something I made up v42.0.1"
argument_list|)
expr_stmt|;
name|IngestDocument
name|ingestDocument
init|=
name|RandomDocumentPicks
operator|.
name|randomIngestDocument
argument_list|(
name|random
argument_list|()
argument_list|,
name|document
argument_list|)
decl_stmt|;
name|processor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|data
init|=
name|ingestDocument
operator|.
name|getSourceAndMetadata
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|data
argument_list|,
name|hasKey
argument_list|(
literal|"target_field"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|target
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|data
operator|.
name|get
argument_list|(
literal|"target_field"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"Other"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"major"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"minor"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"patch"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"build"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"os"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"Other"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"os_name"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"Other"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"os_major"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"os_minor"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target
operator|.
name|get
argument_list|(
literal|"device"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"Other"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
