begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.xcontent.support.filtering
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|support
operator|.
name|filtering
package|;
end_package

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|JsonFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|JsonParser
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|filter
operator|.
name|FilteringGeneratorDelegate
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
name|io
operator|.
name|stream
operator|.
name|BytesStreamOutput
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_class
DECL|class|FilterPathGeneratorFilteringTests
specifier|public
class|class
name|FilterPathGeneratorFilteringTests
extends|extends
name|ESTestCase
block|{
DECL|field|JSON_FACTORY
specifier|private
specifier|final
name|JsonFactory
name|JSON_FACTORY
init|=
operator|new
name|JsonFactory
argument_list|()
decl_stmt|;
DECL|method|testInclusiveFilters
specifier|public
name|void
name|testInclusiveFilters
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|SAMPLE
init|=
literal|"{'a':0,'b':true,'c':'c_value','d':[0,1,2],'e':[{'f1':'f1_value','f2':'f2_value'},{'g1':'g1_value','g2':'g2_value'}],'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
decl_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"a"
argument_list|,
literal|true
argument_list|,
literal|"{'a':0}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"b"
argument_list|,
literal|true
argument_list|,
literal|"{'b':true}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"c"
argument_list|,
literal|true
argument_list|,
literal|"{'c':'c_value'}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"d"
argument_list|,
literal|true
argument_list|,
literal|"{'d':[0,1,2]}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"e"
argument_list|,
literal|true
argument_list|,
literal|"{'e':[{'f1':'f1_value','f2':'f2_value'},{'g1':'g1_value','g2':'g2_value'}]}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h"
argument_list|,
literal|true
argument_list|,
literal|"{'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"z"
argument_list|,
literal|true
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"e.f1"
argument_list|,
literal|true
argument_list|,
literal|"{'e':[{'f1':'f1_value'}]}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"e.f2"
argument_list|,
literal|true
argument_list|,
literal|"{'e':[{'f2':'f2_value'}]}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"e.f*"
argument_list|,
literal|true
argument_list|,
literal|"{'e':[{'f1':'f1_value','f2':'f2_value'}]}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"e.*2"
argument_list|,
literal|true
argument_list|,
literal|"{'e':[{'f2':'f2_value'},{'g2':'g2_value'}]}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h.i"
argument_list|,
literal|true
argument_list|,
literal|"{'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h.i.j"
argument_list|,
literal|true
argument_list|,
literal|"{'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h.i.j.k"
argument_list|,
literal|true
argument_list|,
literal|"{'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h.i.j.k.l"
argument_list|,
literal|true
argument_list|,
literal|"{'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h.*"
argument_list|,
literal|true
argument_list|,
literal|"{'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"*.i"
argument_list|,
literal|true
argument_list|,
literal|"{'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"*.i.j"
argument_list|,
literal|true
argument_list|,
literal|"{'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h.*.j"
argument_list|,
literal|true
argument_list|,
literal|"{'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h.i.*"
argument_list|,
literal|true
argument_list|,
literal|"{'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"*.i.j.k"
argument_list|,
literal|true
argument_list|,
literal|"{'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h.*.j.k"
argument_list|,
literal|true
argument_list|,
literal|"{'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h.i.*.k"
argument_list|,
literal|true
argument_list|,
literal|"{'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h.i.j.*"
argument_list|,
literal|true
argument_list|,
literal|"{'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"*.i.j.k.l"
argument_list|,
literal|true
argument_list|,
literal|"{'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h.*.j.k.l"
argument_list|,
literal|true
argument_list|,
literal|"{'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h.i.*.k.l"
argument_list|,
literal|true
argument_list|,
literal|"{'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h.i.j.*.l"
argument_list|,
literal|true
argument_list|,
literal|"{'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h.i.j.k.*"
argument_list|,
literal|true
argument_list|,
literal|"{'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h.*.j.*.l"
argument_list|,
literal|true
argument_list|,
literal|"{'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"**.l"
argument_list|,
literal|true
argument_list|,
literal|"{'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"**.*2"
argument_list|,
literal|true
argument_list|,
literal|"{'e':[{'f2':'f2_value'},{'g2':'g2_value'}]}"
argument_list|)
expr_stmt|;
block|}
DECL|method|testExclusiveFilters
specifier|public
name|void
name|testExclusiveFilters
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|SAMPLE
init|=
literal|"{'a':0,'b':true,'c':'c_value','d':[0,1,2],'e':[{'f1':'f1_value','f2':'f2_value'},{'g1':'g1_value','g2':'g2_value'}],'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
decl_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"a"
argument_list|,
literal|false
argument_list|,
literal|"{'b':true,'c':'c_value','d':[0,1,2],'e':[{'f1':'f1_value','f2':'f2_value'},{'g1':'g1_value','g2':'g2_value'}],'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"b"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'c':'c_value','d':[0,1,2],'e':[{'f1':'f1_value','f2':'f2_value'},{'g1':'g1_value','g2':'g2_value'}],'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"c"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b':true,'d':[0,1,2],'e':[{'f1':'f1_value','f2':'f2_value'},{'g1':'g1_value','g2':'g2_value'}],'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"d"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b':true,'c':'c_value','e':[{'f1':'f1_value','f2':'f2_value'},{'g1':'g1_value','g2':'g2_value'}],'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"e"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b':true,'c':'c_value','d':[0,1,2],'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b':true,'c':'c_value','d':[0,1,2],'e':[{'f1':'f1_value','f2':'f2_value'},{'g1':'g1_value','g2':'g2_value'}]}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"z"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b':true,'c':'c_value','d':[0,1,2],'e':[{'f1':'f1_value','f2':'f2_value'},{'g1':'g1_value','g2':'g2_value'}],'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"e.f1"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b':true,'c':'c_value','d':[0,1,2],'e':[{'f2':'f2_value'},{'g1':'g1_value','g2':'g2_value'}],'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"e.f2"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b':true,'c':'c_value','d':[0,1,2],'e':[{'f1':'f1_value'},{'g1':'g1_value','g2':'g2_value'}],'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"e.f*"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b':true,'c':'c_value','d':[0,1,2],'e':[{'g1':'g1_value','g2':'g2_value'}],'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"e.*2"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b':true,'c':'c_value','d':[0,1,2],'e':[{'f1':'f1_value'},{'g1':'g1_value'}],'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h.i"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b':true,'c':'c_value','d':[0,1,2],'e':[{'f1':'f1_value','f2':'f2_value'},{'g1':'g1_value','g2':'g2_value'}]}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h.i.j"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b':true,'c':'c_value','d':[0,1,2],'e':[{'f1':'f1_value','f2':'f2_value'},{'g1':'g1_value','g2':'g2_value'}]}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h.i.j.k"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b':true,'c':'c_value','d':[0,1,2],'e':[{'f1':'f1_value','f2':'f2_value'},{'g1':'g1_value','g2':'g2_value'}]}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h.i.j.k.l"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b':true,'c':'c_value','d':[0,1,2],'e':[{'f1':'f1_value','f2':'f2_value'},{'g1':'g1_value','g2':'g2_value'}]}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h.*"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b':true,'c':'c_value','d':[0,1,2],'e':[{'f1':'f1_value','f2':'f2_value'},{'g1':'g1_value','g2':'g2_value'}]}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"*.i"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b':true,'c':'c_value','d':[0,1,2],'e':[{'f1':'f1_value','f2':'f2_value'},{'g1':'g1_value','g2':'g2_value'}]}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"*.i.j"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b':true,'c':'c_value','d':[0,1,2],'e':[{'f1':'f1_value','f2':'f2_value'},{'g1':'g1_value','g2':'g2_value'}]}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h.*.j"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b':true,'c':'c_value','d':[0,1,2],'e':[{'f1':'f1_value','f2':'f2_value'},{'g1':'g1_value','g2':'g2_value'}]}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h.i.*"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b':true,'c':'c_value','d':[0,1,2],'e':[{'f1':'f1_value','f2':'f2_value'},{'g1':'g1_value','g2':'g2_value'}]}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"*.i.j.k"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b':true,'c':'c_value','d':[0,1,2],'e':[{'f1':'f1_value','f2':'f2_value'},{'g1':'g1_value','g2':'g2_value'}]}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h.*.j.k"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b':true,'c':'c_value','d':[0,1,2],'e':[{'f1':'f1_value','f2':'f2_value'},{'g1':'g1_value','g2':'g2_value'}]}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h.i.*.k"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b':true,'c':'c_value','d':[0,1,2],'e':[{'f1':'f1_value','f2':'f2_value'},{'g1':'g1_value','g2':'g2_value'}]}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h.i.j.*"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b':true,'c':'c_value','d':[0,1,2],'e':[{'f1':'f1_value','f2':'f2_value'},{'g1':'g1_value','g2':'g2_value'}]}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"*.i.j.k.l"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b':true,'c':'c_value','d':[0,1,2],'e':[{'f1':'f1_value','f2':'f2_value'},{'g1':'g1_value','g2':'g2_value'}]}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h.*.j.k.l"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b':true,'c':'c_value','d':[0,1,2],'e':[{'f1':'f1_value','f2':'f2_value'},{'g1':'g1_value','g2':'g2_value'}]}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h.i.*.k.l"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b':true,'c':'c_value','d':[0,1,2],'e':[{'f1':'f1_value','f2':'f2_value'},{'g1':'g1_value','g2':'g2_value'}]}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h.i.j.*.l"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b':true,'c':'c_value','d':[0,1,2],'e':[{'f1':'f1_value','f2':'f2_value'},{'g1':'g1_value','g2':'g2_value'}]}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h.i.j.k.*"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b':true,'c':'c_value','d':[0,1,2],'e':[{'f1':'f1_value','f2':'f2_value'},{'g1':'g1_value','g2':'g2_value'}]}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"h.*.j.*.l"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b':true,'c':'c_value','d':[0,1,2],'e':[{'f1':'f1_value','f2':'f2_value'},{'g1':'g1_value','g2':'g2_value'}]}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"**.l"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b':true,'c':'c_value','d':[0,1,2],'e':[{'f1':'f1_value','f2':'f2_value'},{'g1':'g1_value','g2':'g2_value'}]}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|SAMPLE
argument_list|,
literal|"**.*2"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b':true,'c':'c_value','d':[0,1,2],'e':[{'f1':'f1_value'},{'g1':'g1_value'}],'h':{'i':{'j':{'k':{'l':'l_value'}}}}}"
argument_list|)
expr_stmt|;
block|}
DECL|method|testInclusiveFiltersWithDots
specifier|public
name|void
name|testInclusiveFiltersWithDots
parameter_list|()
throws|throws
name|Exception
block|{
name|assertResult
argument_list|(
literal|"{'a':0,'b.c':'value','b':{'c':'c_value'}}"
argument_list|,
literal|"b.c"
argument_list|,
literal|true
argument_list|,
literal|"{'b':{'c':'c_value'}}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
literal|"{'a':0,'b.c':'value','b':{'c':'c_value'}}"
argument_list|,
literal|"b\\.c"
argument_list|,
literal|true
argument_list|,
literal|"{'b.c':'value'}"
argument_list|)
expr_stmt|;
block|}
DECL|method|testExclusiveFiltersWithDots
specifier|public
name|void
name|testExclusiveFiltersWithDots
parameter_list|()
throws|throws
name|Exception
block|{
name|assertResult
argument_list|(
literal|"{'a':0,'b.c':'value','b':{'c':'c_value'}}"
argument_list|,
literal|"b.c"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b.c':'value'}"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
literal|"{'a':0,'b.c':'value','b':{'c':'c_value'}}"
argument_list|,
literal|"b\\.c"
argument_list|,
literal|false
argument_list|,
literal|"{'a':0,'b':{'c':'c_value'}}"
argument_list|)
expr_stmt|;
block|}
DECL|method|assertResult
specifier|private
name|void
name|assertResult
parameter_list|(
name|String
name|input
parameter_list|,
name|String
name|filter
parameter_list|,
name|boolean
name|inclusive
parameter_list|,
name|String
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
try|try
init|(
name|BytesStreamOutput
name|os
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
try|try
init|(
name|FilteringGeneratorDelegate
name|generator
init|=
operator|new
name|FilteringGeneratorDelegate
argument_list|(
name|JSON_FACTORY
operator|.
name|createGenerator
argument_list|(
name|os
argument_list|)
argument_list|,
operator|new
name|FilterPathBasedFilter
argument_list|(
operator|new
name|String
index|[]
block|{
name|filter
block|}
argument_list|,
name|inclusive
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
init|)
block|{
try|try
init|(
name|JsonParser
name|parser
init|=
name|JSON_FACTORY
operator|.
name|createParser
argument_list|(
name|replaceQuotes
argument_list|(
name|input
argument_list|)
argument_list|)
init|)
block|{
while|while
condition|(
name|parser
operator|.
name|nextToken
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|generator
operator|.
name|copyCurrentStructure
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|assertThat
argument_list|(
name|os
operator|.
name|bytes
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|replaceQuotes
argument_list|(
name|expected
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|replaceQuotes
specifier|private
name|String
name|replaceQuotes
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|s
operator|.
name|replace
argument_list|(
literal|'\''
argument_list|,
literal|'"'
argument_list|)
return|;
block|}
block|}
end_class

end_unit

