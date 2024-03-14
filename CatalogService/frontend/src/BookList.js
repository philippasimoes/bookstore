import React, { Component } from 'react';
import { Button, ButtonGroup, Container, Table } from 'reactstrap';
import AppNavbar from './AppNavbar';
import { Link } from 'react-router-dom';

class BookList extends Component {

    constructor(props) {
        super(props);
        this.state = {books: []};
        this.remove = this.remove.bind(this);
    }

    componentDidMount() {
        fetch('/books')
            .then(response => response.json())
            .then(data => this.setState({books: data}));
    }
}
export default BookList;