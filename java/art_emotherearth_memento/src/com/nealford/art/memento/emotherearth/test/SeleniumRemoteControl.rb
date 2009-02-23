require 'test/unit'
require 'test/unit'
require 'test/unit'
require 'selenium'

class SeleniumRemoteControlTest < Test::Unit::TestCase

	def setup
		@s = Selenium::SeleneseInterpreter.new("localhost", 4444, "*chrome", "http://locahost:8080/", 15000)
		@s.start
	end
		
	def test_emotherearth_end_to_end
        @s.open "http://localhost:8080/art_emotherearth_memento/welcome"
        @s.type "user", "Homer"
        @s.click "//input[@id='submitButton']"
        @s.wait_for_page_to_load "30000"
        assert @s.get_location =~ /.*art_emotherearth_memento\/catalog/
        assert_equal "CatalogView", @s.get_title
        assert @s.is_text_present("Catalog of Items")
        assert @s.is_element_present("//html/body/table/")
        assert_equal "Ocean", @s.get_table("//html/body/table/.1.1")
        @s.type "document.forms[1].quantity", "3"
        @s.click "//input[@id='submit2']"
        @s.wait_for_page_to_load "30000"
        assert @s.get_location =~ /.*art_emotherearth_memento\/showcart/
        assert_equal "ShowCart", @s.get_title
        assert @s.is_element_present("link=Click here for more shopping")
        assert @s.is_text_present("*, here is your cart:")
        @s.click "link=Click here for more shopping"
        @s.wait_for_page_to_load "30000"
        assert @s.get_location =~ /.*art_emotherearth_memento\/catalog/
        @s.type "document.forms[3].quantity", "2"
        @s.click "//input[@id='submit4']"
        @s.wait_for_page_to_load "30000"
        @s.type "ccNum", "444444444444"
        @s.select "ccType", "label=Amex"
        @s.type "ccExp", "12/10"
        @s.click "//input[@value='Check out']"
        @s.wait_for_page_to_load "30000"
        assert @s.is_text_present("*, Thank you for shopping at eMotherEarth.com")
        assert @s.is_text_present("regexp:Your confirmation number is \d?")
	end
	
	def teardown
		@s.stop
	end
end